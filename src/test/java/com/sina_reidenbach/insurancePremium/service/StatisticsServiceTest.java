package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.model.Statistics;
import com.sina_reidenbach.insurancePremium.repository.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    @InjectMocks
    private StatisticsService statisticsService;

    @Mock
    private StatisticsRepository statisticsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveStatistics() {
        LocalDateTime dateTime = LocalDateTime.now();
        String postcode = "12345";
        String vehicleName = "TestVehicle";
        int km = 15000;
        double premium = 200.0;
        String ipAddress = "192.168.0.1";

        statisticsService.saveStatistics(dateTime, postcode, vehicleName, km, premium, ipAddress);

        verify(statisticsRepository, times(1)).save(any(Statistics.class));
    }

    @Test
    void testSaveStatisticsWithNullValues() {
        LocalDateTime dateTime = LocalDateTime.now();
        String postcode = null;
        String vehicleName = "TestVehicle";
        int km = 15000;
        double premium = 200.0;
        String ipAddress = "192.168.0.1";

        assertDoesNotThrow(() -> statisticsService.saveStatistics(dateTime, postcode, vehicleName, km, premium, ipAddress));
    }

    @Test
    void testSaveStatisticsCallsRepositoryWithCorrectData() {
        LocalDateTime dateTime = LocalDateTime.now();
        String postcode = "54321";
        String vehicleName = "VehicleX";
        int km = 10000;
        double premium = 150.0;
        String ipAddress = "10.0.0.1";

        statisticsService.saveStatistics(dateTime, postcode, vehicleName, km, premium, ipAddress);

        verify(statisticsRepository).save(argThat(statistics ->
                statistics.getDateTime().equals(dateTime) &&
                        statistics.getPostcode().equals(postcode) &&
                        statistics.getVehicle().equals(vehicleName) &&
                        statistics.getAnnokilometers() == km &&
                        statistics.getPremium() == premium &&
                        statistics.getIpAddress().equals(ipAddress)
        ));
    }
}
