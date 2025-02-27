package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.InsurancePremiumApplication;
import com.sina_reidenbach.insurancePremium.dto.AnnoKilometersResponse;
import com.sina_reidenbach.insurancePremium.dto.PremiumResult;
import com.sina_reidenbach.insurancePremium.model.*;
import com.sina_reidenbach.insurancePremium.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = InsurancePremiumApplication.class)
class EntityServiceTest {
    private AutoCloseable closeable;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ErrorHandlingService errorHandlingService;
    @Mock
    private PostcodeRepository postcodeRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private AnnoKilometersRepository annoKilometersRepository;

    @InjectMocks
    private EntityService entityService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testGetSortedVehicleList() {
        Vehicle v1 = new Vehicle(1L, "Audi", 1.2);
        Vehicle v2 = new Vehicle(2L, "BMW", 1.1);
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(v2, v1));

        List<Vehicle> result = entityService.getSortedVehicleList();

        assertEquals(List.of(v1, v2), result);
    }

    @Test
    void testFindById() {
        Vehicle vehicle = new Vehicle(1L, "Audi", 1.2);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Optional<Vehicle> result = entityService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(vehicle, result.get());
    }

    @Test
    void testGetVehicleFactor_WhenVehicleExists() {
        Vehicle vehicle = new Vehicle(1L, "Audi", 1.2);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        double factor = entityService.getVehicleFactor(1L);
        assertEquals(1.2, factor);
    }

    @Test
    void testGetVehicleFactor_WhenVehicleDoesNotExist() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(errorHandlingService).logAndThrowError(anyString());

        double factor = entityService.getVehicleFactor(1L);
        assertEquals(0, factor);
    }

    @Test
    void testFindPostcode() {
        Postcode postcode = new Postcode("12345");
        when(postcodeRepository.findFirstByPostcodeValue("12345")).thenReturn(Optional.of(postcode));

        Optional<Postcode> result = entityService.findPostcode("12345");
        assertTrue(result.isPresent());
        assertEquals(postcode, result.get());
    }

    @Test
    void testFindCityByPostcode() {
        City city = new City("Berlin");
        when(cityRepository.findByPostcodes_PostcodeValue("10115")).thenReturn(city);

        Optional<City> result = entityService.findCityByPostcode("10115");
        assertTrue(result.isPresent());
        assertEquals("Berlin", result.get().getName());
    }

    @Test
    void testFindRegionByCityName() {
        Region region = new Region("Nord", 1.3);
        when(regionRepository.findByCities_Name("Berlin")).thenReturn(region);

        Optional<Region> result = entityService.findRegionByCityName("Berlin");
        assertTrue(result.isPresent());
        assertEquals("Nord", result.get().getName());
    }

    @Test
    void testFilterPostcodesByInput() {
        Postcode p1 = new Postcode("10115");
        Postcode p2 = new Postcode("10117");
        when(postcodeRepository.findAll()).thenReturn(List.of(p1, p2, new Postcode("20253")));

        List<Postcode> result = entityService.filterPostcodesByInput("101");
        assertEquals(List.of(p1, p2), result);
    }

    @Test
    void testGetSortedPostcodes() {
        Postcode p1 = new Postcode("10115");
        Postcode p2 = new Postcode("10117");
        when(postcodeRepository.findAll()).thenReturn(List.of(p2, p1));

        List<Postcode> result = entityService.getSortedPostcodes();
        assertEquals(List.of(p1, p2), result);
    }

    @Test
    void testGetRegionNameByPostcode_WhenRegionExists() {
        Region region = new Region("West", 1.1);
        when(regionRepository.findByCities_Name("10115")).thenReturn(region);

        String result = entityService.getRegionNameByPostcode("10115");
        assertEquals("West", result);
    }

    @Test
    void testGetRegionNameByPostcode_WhenRegionDoesNotExist() {
        when(regionRepository.findByCities_Name("99999")).thenReturn(null);

        String result = entityService.getRegionNameByPostcode("99999");
        assertEquals("Unbekannt", result);
    }

    @Test
    void testGeneratePostcodeOptions_WithResults() {
        Postcode p1 = new Postcode("10115");
        when(postcodeRepository.findAll()).thenReturn(List.of(p1));

        String result = entityService.generatePostcodeOptions("101");
        assertTrue(result.contains("<option value=\"10115\">10115</option>"));
    }

    @Test
    void testAddPremiumToModel() {
        Model model = mock(Model.class);
        PremiumResult result = new PremiumResult(200.0, "Nord");

        entityService.addPremiumToModel(model, result);

        verify(model).addAttribute("premium", "200,00 €");
        verify(model).addAttribute("region", "Nord");
    }


    @Test
    void testAddDefaultAttributes() {
        Model model = mock(Model.class);
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());
        when(postcodeRepository.findAll()).thenReturn(Collections.emptyList());

        entityService.addDefaultAttributes(model);

        verify(model).addAttribute(eq("vehicleList"), anyList());
        verify(model).addAttribute(eq("postcodeList"), anyList());
    }

    @Test
    void testIsPostcodeValid_WhenPostcodeExists() {
        when(postcodeRepository.existsByPostcodeValue("12345")).thenReturn(true);

        boolean result = entityService.isPostcodeValid("12345");

        assertTrue(result);
    }

    @Test
    void testIsPostcodeValid_WhenPostcodeDoesNotExist() {
        when(postcodeRepository.existsByPostcodeValue("12345")).thenReturn(false);

        boolean result = entityService.isPostcodeValid("12345");

        assertFalse(result);
    }

    @Test
    void testGetRegionFactor_WhenRegionExists() {
        Region region = new Region("West", 1.5);
        when(regionRepository.findByPostcodeValueStartingWith("101")).thenReturn(Optional.of(region));

        double result = entityService.getRegionFactor("101");

        assertEquals(1.5, result);
    }

    @Test
    void testGetRegionFactor_WhenRegionDoesNotExist() {
        when(regionRepository.findByPostcodeValueStartingWith("999")).thenReturn(Optional.empty());
        doNothing().when(errorHandlingService).logAndThrowError(anyString());

        double result = entityService.getRegionFactor("999");

        assertEquals(0, result);
    }

    @Test
    void testGetAnnoKilometers() {
        var km1 = new Anno_Kilometers(1000, 20000, 1.1);
        var km2 = new Anno_Kilometers(20001, 50000, 1.2);
        when(annoKilometersRepository.findAll()).thenReturn(List.of(km1, km2));

        AnnoKilometersResponse result = entityService.getAnnoKilometers();

        assertNotNull(result);
        assertEquals(2, result.getAnnoKilometers().size());
    }

    @Test
    void testGetAnnoKilometers_WhenNoDataFound() {
        when(annoKilometersRepository.findAll()).thenReturn(Collections.emptyList());

        AnnoKilometersResponse result = entityService.getAnnoKilometers();

        assertNotNull(result);
        assertTrue(result.getAnnoKilometers().isEmpty());
    }
}
