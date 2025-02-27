package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.InsurancePremiumApplication;
import com.sina_reidenbach.insurancePremium.model.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = InsurancePremiumApplication.class)
@ActiveProfiles("test")
public class StatisticsRepositoryTest {


    @Autowired
    private StatisticsRepository statisticsRepository;


    @Test
    void testFindByDateTimeAndPostcodeAndVehicle_Found() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 2, 24, 10, 30);
        String postcode = "51373";
        String vehicleName = "Geländewagen (SUV)";
        String ip = "127.0.0.1";

        Statistics entity = new Statistics(dateTime, postcode, vehicleName, 2000, 562.5, ip);
        statisticsRepository.save(entity);

        List<Statistics> allStatistics = statisticsRepository.findAll();
        System.out.println("All stored Statistics:");
        allStatistics.forEach(stat -> System.out.println(stat));

        Optional<Statistics> result = statisticsRepository.findByDateTimeAndPostcodeAndVehicle(dateTime, postcode, vehicleName);
        System.out.println(result);

        assertTrue(result.isPresent(), "Statistik sollte gefunden werden.");
        assertTrue(result.get().getDateTime().equals(dateTime), "DateTime sollte übereinstimmen.");
        assertTrue(result.get().getPostcode().equals(postcode), "Postcode sollte übereinstimmen.");
        assertTrue(result.get().getVehicle().equals(vehicleName), "VehicleName sollte übereinstimmen.");
    }

    @Test
    void testFindByDateTimeAndPostcodeAndVehicle_NotFound() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 2, 24, 10, 30, 0, 0);
        String postcode = "12345";
        String vehicleName = "NonExistentVehicle";

        Optional<Statistics> result = statisticsRepository.findByDateTimeAndPostcodeAndVehicle(dateTime, postcode, vehicleName);

        assertFalse(result.isPresent(), "Statistik sollte nicht gefunden werden.");
    }
}
