package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.model.Statistics;
import com.sina_reidenbach.insurancePremium.repository.StatisticsRepository;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *************************************************************************************************************
 * Verantwortlich für die Speicherung von Statistiken bezüglich Versicherungsprämien und Fahrzeugen.         *
 * Handhabt die Speicherung von Statistiken in der Datenbank und prüft auf Duplikate und gültige IP-Adressen.*
 *************************************************************************************************************
 */
@Service
public class StatisticsService {
    @Autowired
    private StatisticsRepository statisticsRepository;

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    /**
     **********************************************************************************************************
     * Speichert Statistik, wenn keine Duplikate für das gegebene Datum, Postleitzahl, Fahrzeug existieren.   *
     * und nur, wenn eine gültige IP-Adresse vorhanden ist.                                                   *
     * @param dateTime Der Zeitpunkt, an dem die Statistik gespeichert wurde.                                 *
     * @param postcode Die Postleitzahl, die mit der Statistik verknüpft ist.                                 *
     * @param vehicleName Der Name des Fahrzeugs, für das die Statistik gespeichert wird.                     *
     * @param km Die Anzahl der gefahrenen Kilometer.                                                         *
     * @param premium Der berechnete Premiumbetrag.                                                           *
     * @param ipAddress Die IP-Adresse des Nutzers, die zur Validierung erforderlich ist.                     *
     * @throws IllegalArgumentException Wenn die IP-Adresse ungültig, oder die Eingabedaten unvollständig sind*
     **********************************************************************************************************
     */
    public void saveStatistics(LocalDateTime dateTime, String postcode, String vehicleName, int km, double premium, String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            logger.info("[INFO] Keine gültige IP-Adresse gefunden. Statistik wird nicht gespeichert.");
            return;
        }

        var existingEntry = statisticsRepository.findByDateTimeAndPostcodeAndVehicle(dateTime, postcode, vehicleName);
        if (existingEntry.isPresent()) {
            logger.info("Duplicate entry found for vehicle: {}, postcode: {}, dateTime: {}", vehicleName, postcode, dateTime);
            return;
        }

        var entity = new Statistics(dateTime, postcode, vehicleName, km, premium, ipAddress);
        try {
            statisticsRepository.save(entity);
            logger.info("[INFO] Statistik erfolgreich gespeichert: {}", entity);
        } catch (Exception e) {
            logger.warn("[WARNUNG] Fehler beim Speichern der Statistik: {}", e.getMessage());
        }
    }
}
