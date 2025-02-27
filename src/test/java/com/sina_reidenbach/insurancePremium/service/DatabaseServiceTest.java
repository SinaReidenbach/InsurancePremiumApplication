package com.sina_reidenbach.insurancePremium.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.sina_reidenbach.insurancePremium.InsurancePremiumApplication;
import com.sina_reidenbach.insurancePremium.controller.FrontendController;
import com.sina_reidenbach.insurancePremium.dto.PremiumResult;
import com.sina_reidenbach.insurancePremium.model.*;
import com.sina_reidenbach.insurancePremium.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = InsurancePremiumApplication.class)
@ActiveProfiles("prod")
class DatabaseServiceTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private FrontendController frontendController;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private final List<String[]> data = new ArrayList<>();

    @Autowired
    private DatabaseService databaseService;
    @Mock
    private BufferedReader bufferedReader;
    private PremiumResult premiumResult;
    @Autowired
    private AnnoKilometersRepository annoKilometersRepository;
    @Autowired
    private PostcodeRepository postcodeRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws IOException, CsvException {
        regionRepository.deleteAll();
        cityRepository.deleteAll();
        postcodeRepository.deleteAll();
        vehicleRepository.deleteAll();
        annoKilometersRepository.deleteAll();

        MockitoAnnotations.openMocks(this);
        doNothing().when(entityManager).persist(any(Anno_Kilometers.class));

        String csvContent =
                "\"DE\", \"DE-BW\", \"Baden-Württemberg\", \"Freiburg\", \"Breisgau-Hochschwarzwald\", \"Breisach am Rhein\", \"79206\", \"Breisach am Rhein\", , , 48.02819, 7.58273, \"Europe/Berlin\", \"UTC+1\", true, \"A\"\n" +
                        "\"DE\", \"DE-NW\", \"Nordrhein-Westfalen\", \"Arnsberg\", \"Herne\", , \"44627\", \"Herne\", \"Holthausen\", , 51.54731, 7.27946, \"Europe/Berlin\", \"UTC+1\", true, \"A\"";

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            csvReader.readAll().stream()
                    .filter(values -> values.length > 7)
                    .forEach(data::add);
        }
    }


    @Test
    void testCreateAnno_Kilometers() {
        List<Anno_Kilometers> result = databaseService.createAnno_Kilometers();

        assertNotNull(result, "Die Liste sollte nicht null sein");
    }

    @Test
    void testCreateVehicle(){
        List<Vehicle> result = databaseService.createVehicle();
        assertNotNull(result, "Die Liste sollte nicht null sein");
    }

    @Test
    void testCreateRegion(){
        List<Region> result = databaseService.createRegion();

        assertNotNull(result, "Die Liste sollte nicht null sein");
    }

    @Test
    void testCreateCity(){
        Region region = new Region();
        region.setName("Nordrheinwestfalen");
        List<Region> regionList = new ArrayList<>();
        regionList.add(region);

        regionRepository.save(region);

        City result = databaseService.createCity("Leverkusen",regionList.get(0));

        assertNotNull(result, "Die Stadt sollte nicht Null sein");
    }

    @Test
    void testCreatePostcode() {
        Region region = new Region();
        region.setName("Bayern");
        regionRepository.save(region);
        City city = new City();
        city.setName("München");
        city.setRegion(region);
        cityRepository.save(city);


        Postcode result = databaseService.createPostcode("51373",city,region);

        assertNotNull(result, "Der Postcode sollte nicht Null sein");
    }


    @Transactional
    @Test
    void testSaveDataTransactional() {
        databaseService.saveDataTransactional();

        Optional<City> city = cityRepository.findFirstByName("Breisach am Rhein");
        assertTrue(city.isPresent(), "Die Stadt 'Breisach am Rhein' sollte gespeichert sein.");

        Optional<Postcode> postcode = postcodeRepository.findFirstByPostcodeValue("44627");
        assertTrue(postcode.isPresent(), "Die Postleitzahl '79206' sollte gespeichert sein.");

        Optional<Region> region = regionRepository.findByName("Baden-Württemberg");
        assertTrue(region.isPresent(), "Die Stadt 'Herne' sollte gespeichert sein.");
    }
}
