package com.sina_reidenbach.insurancePremium.controller;

import com.sina_reidenbach.insurancePremium.InsurancePremiumApplication;
import com.sina_reidenbach.insurancePremium.dto.PremiumResult;
import com.sina_reidenbach.insurancePremium.model.Vehicle;
import com.sina_reidenbach.insurancePremium.service.*;
import com.sina_reidenbach.insurancePremium.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = InsurancePremiumApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class FrontendControllerTest {
    private AutoCloseable closeable;
    private MockMvc mockMvc;
    @Mock
    private CalculateService calculateService;
    @Mock
    private EntityService entityService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;
    @Mock
    private Vehicle vehicle;
    @Mock
    private ErrorHandlingService errorHandlingService;

    @InjectMocks
    private FrontendController frontendController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(frontendController, "entityService", entityService);
        mockMvc = MockMvcBuilders.standaloneSetup(frontendController).build();
    }
    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testBerechnen_WithValidData() {
        String validPostcode = "12345";
        Long vehicleId = 1L;
        int km = 1000;
        PremiumResult premiumResult = new PremiumResult(500.0, "Baden-Württemberg");

        when(entityService.isPostcodeValid(validPostcode)).thenReturn(true);
        when(entityService.getSortedVehicleList()).thenReturn(Collections.singletonList(vehicle));
        when(calculateService.calculateAndSavePremiumWithStatistics(km, validPostcode, vehicleId, IpUtils.getClientIp(request)))
                .thenReturn(500.0);
        when(entityService.getRegionNameByPostcode(validPostcode)).thenReturn("Baden-Württemberg");

        doAnswer(invocation -> {
            Model modelArg = invocation.getArgument(0);
            modelArg.addAttribute("premium", "500,00 €");
            modelArg.addAttribute("region", "Baden-Württemberg");
            return null;
        }).when(entityService).addPremiumToModel(any(Model.class), any(PremiumResult.class));

        String result = frontendController.berechnen(km, validPostcode, vehicleId, request, model);

        assertEquals("index", result);
        verify(model).addAttribute("premium", "500,00 €");
        verify(model).addAttribute("region", "Baden-Württemberg");
        verify(model).addAttribute("vehicleList", List.of(vehicle));
    }


    @Test
    void testBerechnen_withMissingParameters_returnsErrorPage() throws Exception {
        mockMvc.perform(post("/berechnen")
                        .param("km", "")
                        .param("postcodeValue", "")
                        .param("vehicle", "")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFilterPostcodes_withNoMatchingInput_returnsEmptyOptions() throws Exception {
        when(entityService.generatePostcodeOptions("9999")).thenReturn("<option value=\"\">Keine Postleitzahlen gefunden</option>");

        mockMvc.perform(get("/filter-postcodes").param("input", "9999"))
                .andExpect(status().isOk())
                .andExpect(content().string("<option value=\"\">Keine Postleitzahlen gefunden</option>"));
    }

    @Test
    void testFilterPostcodes_withMatchingInput_returnsFilteredPostcodes() throws Exception {
        when(entityService.generatePostcodeOptions("7017"))
                .thenReturn("<option value=\"70173\">70173</option><option value=\"70174\">70174</option>");

        mockMvc.perform(get("/filter-postcodes").param("input", "7017"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<option value=\"70173\">70173</option>")))
                .andExpect(content().string(containsString("<option value=\"70174\">70174</option>")));
    }

    @Test
    void testBerechnenInvalidPostcode() throws Exception {
        when(entityService.isPostcodeValid("00000")).thenReturn(false);

        mockMvc.perform(post("/berechnen")
                        .param("km", "15000")
                        .param("postcodeValue", "00000")
                        .param("vehicleId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(errorHandlingService, times(1)).handleError(any(Model.class), eq("Ungültige Postleitzahl"));
    }

    @Test
    void testBerechnenUnexpectedError() throws Exception {
        when(entityService.isPostcodeValid(anyString())).thenThrow(new RuntimeException("Datenbankfehler"));

        mockMvc.perform(post("/berechnen")
                        .param("km", "15000")
                        .param("postcodeValue", "12345")
                        .param("vehicleId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(errorHandlingService, times(1)).handleError(any(Model.class), eq("Ein unerwarteter Fehler ist aufgetreten."));
    }

    @Test
    void testShowHomePage_ShouldAddDefaultAttributesAndReturnIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(entityService, times(1)).addDefaultAttributes(any(Model.class));
    }
    @Test
    void testBerechnen_EmptyVehicleList() throws Exception {
        when(entityService.isPostcodeValid("12345")).thenReturn(true);
        when(entityService.getSortedVehicleList()).thenReturn(Collections.emptyList());
        when(calculateService.calculateAndSavePremiumWithStatistics(anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(500.0);
        when(entityService.getRegionNameByPostcode("12345")).thenReturn("Baden-Württemberg");

        mockMvc.perform(post("/berechnen")
                        .param("km", "10000")
                        .param("postcodeValue", "12345")
                        .param("vehicleId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(model, never()).addAttribute(eq("vehicleList"), anyList());
    }
    @Test
    void testBerechnen_WithOnlyKilometers() throws Exception {
        mockMvc.perform(post("/berechnen")
                        .param("km", "15000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBerechnen_WithOnlyPostcode() throws Exception {
        mockMvc.perform(post("/berechnen")
                        .param("postcodeValue", "12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBerechnen_CalculationThrowsException() throws Exception {
        when(entityService.isPostcodeValid("12345")).thenReturn(true);
        when(calculateService.calculateAndSavePremiumWithStatistics(anyInt(), anyString(), anyLong(), anyString()))
                .thenThrow(new RuntimeException("Berechnungsfehler"));

        mockMvc.perform(post("/berechnen")
                        .param("km", "15000")
                        .param("postcodeValue", "12345")
                        .param("vehicleId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(errorHandlingService).handleError(any(Model.class), eq("Ein unerwarteter Fehler ist aufgetreten."));
    }

    @Test
    void testBerechnen_WithGetRequest_ShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/berechnen"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testBerechnen_InvalidVehicleId() throws Exception {
        when(entityService.isPostcodeValid("12345")).thenReturn(true);
        when(calculateService.calculateAndSavePremiumWithStatistics(anyInt(), anyString(), eq(9999L), anyString()))
                .thenThrow(new IllegalArgumentException("Fahrzeug nicht gefunden"));

        mockMvc.perform(post("/berechnen")
                        .param("km", "10000")
                        .param("postcodeValue", "12345")
                        .param("vehicleId", "9999"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(errorHandlingService).handleError(any(Model.class), eq("Fahrzeug nicht gefunden"));
    }

    @Test
    void testBerechnen_NullPointer() throws Exception {

        mockMvc.perform(post("/berechnen")
                        .param("km", "15000")
                        .param("postcodeValue", "12345")
                        .param("vehicleId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(errorHandlingService).handleError(any(Model.class), eq("Ungültige Postleitzahl"));
    }
}

