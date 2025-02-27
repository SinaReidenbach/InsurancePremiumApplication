package com.sina_reidenbach.insurancePremium.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import com.sina_reidenbach.insurancePremium.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *************************************************************************************************************
 * Repository-Interface für den Zugriff auf {@link Statistics}-Entitäten.                                    *
 * Ermöglicht das Speichern und Abrufen von Statistikdaten zu Berechnungen von Versicherungsprämien.         *
 *************************************************************************************************************
 */
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    /**
     *********************************************************************************************************
     * Findet eine {@link Statistics}-Entität anhand von Datum, Postleitzahl und Fahrzeugname.               *
     * Dient zum Abrufen bereits gespeicherter Berechnungsstatistiken mit den angegebenen Parametern.        *
     *                                                                                                       *
     * @param dateTime    Das Datum und die Uhrzeit der Berechnung.                                          *
     * @param postcode    Die Postleitzahl, die bei der Berechnung verwendet wurde.                          *
     * @param vehicleName Der Name des Fahrzeugs, für das die Prämie berechnet wurde.                        *
     * @return Ein {@link Optional} mit der gefundenen {@link Statistics}-Entität oder leer, wenn keine       *
     *         übereinstimmenden Daten vorliegen.                                                             *
     *********************************************************************************************************
     */
    Optional<Statistics> findByDateTimeAndPostcodeAndVehicle(LocalDateTime dateTime, String postcode, String vehicleName);
}
