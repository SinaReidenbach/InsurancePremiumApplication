package com.sina_reidenbach.insurancePremium.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina_reidenbach.insurancePremium.dto.*;
import com.sina_reidenbach.insurancePremium.repository.*;
import com.sina_reidenbach.insurancePremium.service.CalculateService;
import com.sina_reidenbach.insurancePremium.service.EntityService;
import com.sina_reidenbach.insurancePremium.service.ErrorHandlingService;
import com.sina_reidenbach.insurancePremium.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ThirdPartyController.class)
public class ThirdPartyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    ErrorResponse errorResponse;
    @MockitoBean
    private ErrorHandlingService errorHandlingService;
    @MockitoBean
    private CalculateService calculateService;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private RegionRepository regionRepository;

    @MockitoBean
    private AnnoKilometersRepository annoKilometersRepository;

    @MockitoBean
    private PostcodeRepository postcodeRepository;
    @MockitoBean
    private StatisticsService statisticsService;
    @MockitoBean
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private ThirdPartyController thirdPartyController;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private EntityService entityService;
    private AnnoKilometersResponse annoKilometersResponse;
    private RegionResponse regionResponse;


    @BeforeEach
    void setUp() {
        List<Map<String, Object>> annoKilometers = List.of(
                Map.of("range", "0-1000"),
                Map.of("range", "1001-10000"),
                Map.of("range", "10001-20000"),
                Map.of("range", "20001-99999")
        );
        annoKilometersResponse = new AnnoKilometersResponse();
        annoKilometersResponse.setAnnoKilometers(annoKilometers);

        Map<Long, Map<String, Object>> regions = Map.of(
                1L, Map.of("region", "Baden-Württemberg"),
                2L, Map.of("region", "Bavaria"),
                3L, Map.of("region", "North Rhine-Westphalia")
        );
        regionResponse = new RegionResponse();
        regionResponse.setRegions(regions);
    }


    @Test
    public void testGetVehicles() throws Exception {
        VehicleResponse vehicleResponse = createVehicleResponse();

        when(entityService.getVehicles()).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/options/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicles['1'].vehicleName").value("SUV"))
                .andExpect(jsonPath("$.vehicles['1'].factor").value(1.5))
                .andExpect(jsonPath("$.vehicles['2'].vehicleName").value("Motorrad"))
                .andExpect(jsonPath("$.vehicles['2'].factor").value(2.0));
    }

    private VehicleResponse createVehicleResponse() {
        VehicleResponse vehicleResponse = new VehicleResponse();

        Map<Long, Map<String, Object>> vehicles = new HashMap<>();
        Map<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("vehicleName", "SUV");
        vehicle1.put("factor", 1.5);
        vehicles.put(1L, vehicle1);

        Map<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("vehicleName", "Motorrad");
        vehicle2.put("factor", 2.0);
        vehicles.put(2L, vehicle2);

        vehicleResponse.setVehicles(vehicles);
        return vehicleResponse;
    }

    @Test
    void testGetRegions() throws Exception {
        when(entityService.getRegions()).thenReturn(regionResponse);
        ResultActions resultActions = mockMvc.perform(get("/api/options/regions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.regions['1'].region").value("Baden-Württemberg"))
                .andExpect(jsonPath("$.regions['2'].region").value("Bavaria"))
                .andExpect(jsonPath("$.regions['3'].region").value("North Rhine-Westphalia"));

        verify(entityService, times(1)).getRegions();
    }

    @Test
    void testGetAnnoKilometers() throws Exception {
        when(entityService.getAnnoKilometers()).thenReturn(annoKilometersResponse);

        ResultActions resultActions = mockMvc.perform(get("/api/options/annoKilometers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.annoKilometers[0].range").value("0-1000"))
                .andExpect(jsonPath("$.annoKilometers[1].range").value("1001-10000"))
                .andExpect(jsonPath("$.annoKilometers[2].range").value("10001-20000"))
                .andExpect(jsonPath("$.annoKilometers[3].range").value("20001-99999"));

        verify(entityService, times(1)).getAnnoKilometers();
    }

    @Test
    public void testCalculatePremium_InvalidData() throws Exception {
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("postcode", "InvalidPostcode");
        invalidData.put("vehicle", "InvalidVehicle");

        List<String> errorMessages = Arrays.asList("Invalid postcode", "Invalid vehicle type");
        when(calculateService.validatePremiumRequest(any(), any())).thenReturn(errorMessages);

        ErrorResponse errorResponse = new ErrorResponse("Validation Error", "Invalid input data: Invalid postcode, Invalid vehicle type");
        when(errorHandlingService.handleValidationErrors(any())).thenReturn(
                ResponseEntity.badRequest().body(errorResponse)
        );

        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Invalid input data: Invalid postcode, Invalid vehicle type"));
    }

        @Test
        public void testCalculatePremium_ValidData () throws Exception {
            when(calculateService.validatePremiumRequest(any(), any())).thenReturn(new ArrayList<>());
            when(calculateService.calculateAndBuildResponse(any())).thenReturn(new PremiumResponse());


            String jsonRequest = "{\"kmMin\":2000,\"kmMax\":2000,\"vehicleId\":14,\"postcode\":\"51373\"}";

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isOk());

            verify(calculateService, times(1)).validatePremiumRequest(any(), any());
            verify(calculateService, times(1)).calculateAndBuildResponse(any());

        }

        @Test
        public void testCalculatePremium_ServerError () throws Exception {
            when(calculateService.validatePremiumRequest(any(), any())).thenReturn(new ArrayList<>());
            when(calculateService.calculateAndBuildResponse(any())).thenThrow(new RuntimeException("Internal Server Error"));

            ErrorResponse errorResponse = new ErrorResponse("Server Error", "Internal Server Error");
            when(errorHandlingService.handleServerError(any())).thenReturn(
                    ResponseEntity.status(500).body(errorResponse)
            );

            String jsonRequest = "{\"kmMin\":2000,\"kmMax\":2000,\"vehicleId\":14,\"postcode\":\"51373\"}";

            mockMvc.perform(post("/api/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Server Error"))
                    .andExpect(jsonPath("$.message").value("Internal Server Error"));

            verify(calculateService, times(1)).validatePremiumRequest(any(), any());
            verify(calculateService, times(1)).calculateAndBuildResponse(any());
        }
    }