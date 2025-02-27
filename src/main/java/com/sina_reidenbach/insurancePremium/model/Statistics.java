package com.sina_reidenbach.insurancePremium.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 *************************************************************************************************************
 * Repräsentiert die Entität für eine Versicherungsstatistik.                                                *
 * Speichert Daten zu einer Berechnung, wie Datum, Postleitzahl, Fahrzeug, Kilometerstand und Prämie.        *
 *************************************************************************************************************
 */
@Getter
@Setter
@Entity
public class Statistics {

    /**
     *********************************************************************************************************
     * Die eindeutige ID der Statistik. Wird in der Datenbank zur Identifikation des Datensatzes verwendet.    *
     *********************************************************************************************************
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;
    private String postcode;
    private String vehicle;
    private int annokilometers;
    private double premium;
    private String ipAddress;

    public Statistics() {
    }

    /**
     *********************************************************************************************************
     * Konstruktor, um eine Statistik mit den angegebenen Werten zu erstellen.                               *
     *                                                                                                       *
     * @param dateTime Das Datum und die Uhrzeit der Berechnung.                                             *
     * @param postcode Die Postleitzahl des Gebiets.                                                         *
     * @param vehicle Das Fahrzeug, für das die Prämie berechnet wurde.                                      *
     * @param annokilometers Der Kilometerstand des Fahrzeugs.                                               *
     * @param premium Die berechnete Versicherungsprämie.                                                    *
     * @param ipAddress Die IP-Adresse des Clients.                                                          *
     *********************************************************************************************************
     */
    public Statistics(LocalDateTime dateTime, String postcode, String vehicle, int annokilometers, double premium, String ipAddress) {
        this.dateTime = dateTime;
        this.postcode = postcode;
        this.vehicle = vehicle;
        this.annokilometers = annokilometers;
        this.premium = premium;
        this.ipAddress = ipAddress;
    }
}
