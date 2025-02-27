package com.sina_reidenbach.insurancePremium.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.sina_reidenbach.insurancePremium.model.*;
import com.sina_reidenbach.insurancePremium.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *************************************************************************************************************
 * Verantwortlich für das Einlesen und Speichern von Daten aus einer CSV-Datei in die Datenbank.             *
 * Handhabt die Erstellung und Speicherung von Entitäten wie Anno_Kilometers, Vehicle, Region, City,         *
 * und Postcode.                                                                                             *
 *************************************************************************************************************
 */
@Profile("prod")
@Service
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private final List<String[]> data = new ArrayList<>();

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PostcodeRepository postcodeRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CityRepository cityRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     *********************************************************************************************************
     * Wird beim Starten der Anwendung ausgeführt und initialisiert den Datenimport sowie das Speichern in   *
     * der Datenbank.                                                                                        *
     *********************************************************************************************************
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        logger.info("✅ DatabaseService gestartet!");
        readCSV();
        var proxy = applicationContext.getBean(DatabaseService.class);
        proxy.saveDataTransactional();
    }

    /**
     *********************************************************************************************************
     * Liest die CSV-Datei mit den Postleitzahlen und extrahiert die relevanten Daten.                       *
     *********************************************************************************************************
     */
    public void readCSV() {
        try (InputStream inputStream = new ClassPathResource("postcodes.csv").getInputStream();
             Reader reader = new InputStreamReader(inputStream);
             var csvReader = new CSVReader(reader)) {

            var rows = csvReader.readAll();
            var firstLine = true;

            for (String[] values : rows) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (values.length > 7) {
                    data.add(values);
                }
            }
        } catch (IOException | CsvException e) {
            logger.error("❌ Fehler beim Lesen der Datei: {}", e.getMessage());
        }
    }

    /**
     *********************************************************************************************************
     * Erstellt eine Liste von Anno_Kilometers-Objekten und speichert sie in der Datenbank.                  *
     *********************************************************************************************************
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Anno_Kilometers> createAnno_Kilometers() {
        var annoKilometersList = Arrays.asList(
                new Anno_Kilometers(0, 5000, 0.5),
                new Anno_Kilometers(5001, 10000, 1.0),
                new Anno_Kilometers(10001, 20000, 1.5),
                new Anno_Kilometers(20001, Integer.MAX_VALUE, 2.0));

        for (Anno_Kilometers annoKilometers : annoKilometersList) {
            entityManager.persist(annoKilometers);
            entityManager.flush();
            entityManager.clear();
        }
        return annoKilometersList;
    }

    /**
     *********************************************************************************************************
     * Erstellt eine Liste von Vehicle-Objekten und speichert sie in der Datenbank.                          *
     *********************************************************************************************************
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Vehicle> createVehicle() {
        var vehicleList = Arrays.asList(
                new Vehicle("Pkw Kraftstoff", 1.5),
                new Vehicle("Lkw ohne Anhänger", 1.5),
                new Vehicle("Motorrad", 2.0),
                new Vehicle("Fahrrad", 1.5),
                new Vehicle("Bus", 1.0),
                new Vehicle("Traktor", 0.5),
                new Vehicle("E-Scooter", 1.5),
                new Vehicle("Roller (Motor)", 2.0),
                new Vehicle("PKW Elektro", 1.0),
                new Vehicle("Wohnmobil", 1.0),
                new Vehicle("Taxi", 1.5),
                new Vehicle("Transporter", 1.5),
                new Vehicle("Lkw mit Anhänger", 2.0),
                new Vehicle("Geländewagen (SUV)", 1.5),
                new Vehicle("Moped", 2.0));

        for (Vehicle vehicle : vehicleList) {
            entityManager.persist(vehicle);
            entityManager.flush();
            entityManager.clear();
        }
        return vehicleList;
    }

    /**
     *********************************************************************************************************
     * Erstellt eine Liste von Region-Objekten und speichert sie in der Datenbank.                           *
     *********************************************************************************************************
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Region> createRegion() {

        var regionList = Arrays.asList(
                new Region("Baden-Württemberg", 1.5),
                new Region("Bayern", 1.5),
                new Region("Berlin", 2.0),
                new Region("Brandenburg", 1.0),
                new Region("Bremen", 1.5),
                new Region("Hamburg", 1.5),
                new Region("Hessen", 1.0),
                new Region("Mecklenburg-Vorpommern", 0.5),
                new Region("Niedersachsen", 1.0),
                new Region("Nordrhein-Westfalen", 1.5),
                new Region("Rheinland-Pfalz", 1.0),
                new Region("Saarland", 1.0),
                new Region("Sachsen", 1.0),
                new Region("Sachsen-Anhalt", 0.5),
                new Region("Schleswig-Holstein", 0.5),
                new Region("Thüringen", 1.0));

        for (Region region : regionList) {
            entityManager.persist(region);
            entityManager.flush();
            entityManager.clear();
        }
        var count = (long) entityManager.createQuery("SELECT COUNT(r) FROM Region r").getSingleResult();

        return regionList;
    }

    /**
     *********************************************************************************************************
     * Erstellt eine City, wenn diese noch nicht existiert, und speichert sie in der Datenbank.              *
     *********************************************************************************************************
     * @param cityName Der Name der Stadt, die erstellt oder gefunden werden soll.                           *
     * @param region Die Region, der die Stadt zugeordnet wird.                                               *
     * @return Die erstellte oder gefundene City.                                                             *
     *********************************************************************************************************
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public City createCity(String cityName, Region region) {
        return cityRepository.findFirstByName(cityName)
                .orElseGet(() -> {
                    var city = new City();
                    city.setName(cityName);
                    city.setRegion(region);
                    entityManager.persist(city);
                    entityManager.flush();
                    entityManager.clear();
                    return city;
                });
    }

    /**
     *********************************************************************************************************
     * Erstellt ein Postcode, wenn dieses noch nicht existiert, und speichert es in der Datenbank.           *
     *********************************************************************************************************
     * @param value Der Wert der Postleitzahl, die erstellt oder gefunden werden soll.                      *
     * @param city Die Stadt, die der Postleitzahl zugeordnet wird.                                           *
     * @param region Die Region, die der Postleitzahl zugeordnet wird.                                        *
     * @return Das erstellte oder gefundene Postcode-Objekt.                                                  *
     *********************************************************************************************************
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Postcode createPostcode(String value, City city, Region region) {
        return postcodeRepository.findFirstByPostcodeValue(value)
                .orElseGet(() -> {
                    var postcode = new Postcode();
                    postcode.setPostcodeValue(value);
                    postcode.setCity(city);
                    postcode.setRegion(region);
                    entityManager.persist(postcode);
                    entityManager.flush();
                    entityManager.clear();
                    return postcode;
                });
    }

    /**
     *********************************************************************************************************
     * Speichert alle Daten aus der CSV-Datei in der Datenbank.                                              *
     * Erstellt und speichert die Tabellen Anno_Kilometers, Vehicle, Region, City und Postcode.              *
     * Wenn Daten fehlen oder ungültig sind, wird eine Warnung ausgegeben.                                   *
     *********************************************************************************************************
     */
    @Transactional(noRollbackFor = Exception.class)
    public void saveDataTransactional() {

        try {
            if (data.isEmpty()) {
                logger.warn("⚠️ Keine Daten zum Speichern!");
                return;
            }

            var annoKilometersList = createAnno_Kilometers();
            logger.info("🚀 Tabelle Anno_kilometers wurde erstellt und befüllt");
            var vehicleList = createVehicle();
            logger.info("🚀 Tabelle Vehicle wurde erstellt und befüllt");
            var regionList = createRegion();
            logger.info("🚀 Tabelle Region wurde erstellt und befüllt");
            logger.info("🚀 einen Moment bitte....");

            for (String[] row : data) {
                var regionName = row[2].replace("\"", "").trim();
                var value = row[6].replace("\"", "").trim();
                var cityName = row[7].replace("\"", "").trim();


                if (value.length() != 5 || !value.matches("\\d{5}")) {
                    logger.warn("⚠️ Ungültige oder fehlende Postleitzahl: {}", value);
                }

                if (regionName.isEmpty() || cityName.isEmpty()) {
                    logger.warn("⚠️ Fehlende Region oder Stadt für Postleitzahl: {}", value);
                }

                if (!regionName.isEmpty() && !cityName.isEmpty() && !value.isEmpty() && value.matches("\\d{5}")) {
                    var optionalRegion = regionRepository.findByName(regionName);
                    if (optionalRegion.isEmpty()) {
                        logger.warn("⚠️ Region nicht gefunden: {}", regionName);
                        continue;
                    }
                    var region = optionalRegion.get();
                    var city = createCity(cityName, region);
                    var postcode = createPostcode(value, city, region);
                }
            }
            logger.info("🚀 Tabellen Postcode und City wurden erstellt und befüllt");
            entityManager.flush();
            entityManager.clear();

            logger.info("✅ Alle Daten wurden erfolgreich gespeichert!");

        } catch (Exception e) {
            logger.error("❌ Fehler beim Speichern der Daten: {}", e.getMessage(), e);
        }
    }
}
