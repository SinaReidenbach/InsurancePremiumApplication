package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.InsurancePremiumApplication;
import com.sina_reidenbach.insurancePremium.dto.PremiumResponse;
import com.sina_reidenbach.insurancePremium.model.*;
import com.sina_reidenbach.insurancePremium.repository.AnnoKilometersRepository;
import com.sina_reidenbach.insurancePremium.repository.PostcodeRepository;
import com.sina_reidenbach.insurancePremium.repository.RegionRepository;
import com.sina_reidenbach.insurancePremium.repository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestPropertySource("classpath:application.properties")
@SpringBootTest(classes = InsurancePremiumApplication.class)
class CalculateServiceTest {
    private AutoCloseable closeable;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private PostcodeRepository postcodeRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private AnnoKilometersRepository annoKilometersRepository;
    @Mock
    private EntityService entityService;
    @Mock
    private StatisticsService statisticsService;
    @Mock
    private PremiumResponse premiumResponse;
    @Autowired
    private CalculateService calculateService;


    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        Region region = new Region();
        region.setFactor(1.5);

        Vehicle vehicle = new Vehicle();
        vehicle.setFactor(1.5);

        Anno_Kilometers annoKilometers = new Anno_Kilometers();
        annoKilometers.setFactor(1.0);


        when(annoKilometersRepository.findByMinLessThanEqualAndMaxGreaterThanEqual(2000, 2000))
                .thenReturn(Collections.singletonList(new Anno_Kilometers(2000, 2000, 1.0)));

        when(vehicleRepository.findById(14L))
                .thenReturn(Optional.of(new Vehicle(14L, "SUV", 1.5)));

        when(regionRepository.findByPostcodeValueStartingWith("51373"))
                .thenReturn(Optional.of(new Region("Nordrhein-Westfalen", 1.5)));

    }
    @AfterEach
    void tearDown() throws Exception {
        // Die Ressourcen im tearDown schließen
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testCalculateRegionFactor() {
        double regionFactor = calculateService.calculateRegionFactor("51373");
        assertEquals(1.5, regionFactor);
    }
    @Test
    void testCalculateVehicleFactor() {
        double vehicleFactor = calculateService.calculateVehicleFactor(14L);
        assertEquals(1.5, vehicleFactor);
    }

    @Test
    void testCalculateAnnoKilometersFactor() {
        when(annoKilometersRepository.findByMinLessThanEqualAndMaxGreaterThanEqual(2000,2000))
                .thenReturn(Collections.singletonList(new Anno_Kilometers(2000,2000,0.5)));

        double kmFactor = calculateService.calculateAnnoKilometersFactor(2000, 2000);

        assertEquals(0.5, kmFactor);

    }

    @Test
    void testCalculatePremium() {
        int kmMin = 2000;
        int kmMax = 2000;
        Long vehicleId = 14L;
        String postcode = "51373";

        double result = calculateService.calculatePremium(vehicleId, kmMax, postcode);

        double expectedPremium = 562.5;
        assertEquals(expectedPremium, result, 0.001);
    }

    @Test
    void testValidatePremiumRequest_vehicleIdIsNull() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("annoKilometers", 1000);
        premiumRequest.put("postcode", "12345");

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("vehicleId muss angegeben werden."));
        assertFalse(errorMessages.contains("annoKilometers muss angegeben werden."));
        assertFalse(errorMessages.contains("postcode muss angegeben werden."));
    }

    @Test
    void testValidatePremiumRequest_annoKilometersIsNull() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 1L);
        premiumRequest.put("postcode", "12345");

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("annoKilometers muss angegeben werden."));
        assertFalse(errorMessages.contains("vehicleId muss angegeben werden."));
        assertFalse(errorMessages.contains("postcode muss angegeben werden."));
    }

    @Test
    void testValidatePremiumRequest_postcodeIsNull() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 1L);
        premiumRequest.put("annoKilometers", 1000);

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("postcode muss angegeben werden."));
        assertFalse(errorMessages.contains("vehicleId muss angegeben werden."));
        assertFalse(errorMessages.contains("annoKilometers muss angegeben werden."));
    }

    @Test
    void testValidatePremiumRequest_invalidVehicleId() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 999L);
        premiumRequest.put("annoKilometers", 1000);
        premiumRequest.put("postcode", "12345");

        when(vehicleRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("Kein Fahrzeug mit der angegebenen ID gefunden."));
    }

    @Test
    void testValidatePremiumRequest_invalidPostcode() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 1L);
        premiumRequest.put("annoKilometers", 1000);
        premiumRequest.put("postcode", "99999");

        when(postcodeRepository.findFirstByPostcodeValue("99999")).thenReturn(java.util.Optional.empty());

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("Kein Postleitzahl-Eintrag für den angegebenen Wert gefunden."));
    }

    @Test
    void testValidatePremiumRequest_invalidAnnoKilometers() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 1L);
        premiumRequest.put("annoKilometers", -500);
        premiumRequest.put("postcode", "12345");

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("Kilometerzahl muss größer als 0 sein."));
    }

    @Test
    void testValidatePremiumRequest_validRequest() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 14L);
        premiumRequest.put("annoKilometers", 2000);
        premiumRequest.put("postcode", "51373");

        when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(new Vehicle()));
        when(postcodeRepository.findFirstByPostcodeValue("12345")).thenReturn(java.util.Optional.of(new Postcode()));

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.isEmpty(), "Es sollten keine Fehler auftreten.");
    }

    @Test
    void testCalculateAndSavePremiumWithStatistics_valid() {
        int annoKilometers = 2000;
        String postcodeValue = "51373";
        Long vehicleId = 14L;
        String ipAddress = "192.168.0.1";

        Postcode postcode = new Postcode();
        postcode.setPostcodeValue(postcodeValue);

        City city = new City();
        city.setName("TestCity");

        Region region = new Region();
        region.setFactor(1.5);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setName("SUV");
        vehicle.setFactor(1.5);

        when(entityService.findPostcode(postcodeValue)).thenReturn(Optional.of(postcode));
        when(entityService.findCityByPostcode(postcodeValue)).thenReturn(Optional.of(city));
        when(entityService.findRegionByCityName(city.getName())).thenReturn(Optional.of(region));
        when(entityService.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        doNothing().when(statisticsService).saveStatistics(any(), any(), any(), anyInt(), anyDouble(), anyString());

        double premium = calculateService.calculateAndSavePremiumWithStatistics(annoKilometers, postcodeValue, vehicleId, ipAddress);

        assertEquals(562.5, premium, 0.001);
    }

    @Test
    void testCalculateAndSavePremiumWithStatistics_invalidPostcode() {
        int annoKilometers = 2000;
        String postcodeValue = "99999";
        Long vehicleId = 14L;
        String ipAddress = "192.168.0.1";

        when(entityService.findPostcode(postcodeValue)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> calculateService.calculateAndSavePremiumWithStatistics(annoKilometers, postcodeValue, vehicleId, ipAddress));
    }

    @Test
    void testCalculateAndBuildResponse() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 14L);
        premiumRequest.put("annoKilometers", 2000);
        premiumRequest.put("postcode", "51373");

        when(vehicleRepository.findById(14L)).thenReturn(Optional.of(new Vehicle(14L, "SUV", 1.5)));
        when(postcodeRepository.findFirstByPostcodeValue("51373")).thenReturn(Optional.of(new Postcode("51373")));

        PremiumResponse response = calculateService.calculateAndBuildResponse(premiumRequest);

        assertNotNull(response);
        assertEquals(562.5, response.getPremium().get("premium"));
    }

    @Test
    void testCalculateAndBuildResponse_missingValues() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 14L);
        premiumRequest.put("annoKilometers", 2000);

        assertThrows(RuntimeException.class, () -> calculateService.calculateAndBuildResponse(premiumRequest));
    }

    @Test
    void testCalculateAndBuildResponse_invalidPostcode() {
        Map<String, Object> premiumRequest = new HashMap<>();
        premiumRequest.put("vehicleId", 14L);
        premiumRequest.put("annoKilometers", 2000);
        premiumRequest.put("postcode", "99999");

        when(vehicleRepository.findById(14L)).thenReturn(Optional.of(new Vehicle(14L, "SUV", 1.5)));
        when(postcodeRepository.findFirstByPostcodeValue("99999")).thenReturn(Optional.empty());

        List<String> errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        assertTrue(errorMessages.contains("Kein Postleitzahl-Eintrag für den angegebenen Wert gefunden."));
    }

}