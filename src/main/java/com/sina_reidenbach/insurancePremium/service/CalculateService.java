package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.dto.PremiumResponse;
import com.sina_reidenbach.insurancePremium.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *********************************************************************************************************************
 * Service zur Berechnung der Versicherungsprämien.                                                                  *
 *                                                                                                                   *
 * Dieser Service bietet Methoden zur Berechnung von Prämien auf Basis von Fahrzeugtyp, Jahreskilometerleistung      *
 * und Region (ermittelt über die Postleitzahl). Neben der Berechnung werden Statistikdaten gespeichert.             *
 *                                                                                                                   *
 * Die berechnete Prämie basiert auf folgenden Faktoren:                                                             *
 * - Fahrzeugfaktor: Abhängig von der Fahrzeug-ID                                                                    *
 * - Kilometerfaktor: Basierend auf dem angegebenen Kilometerbereich                                                 *
 * - Regionsfaktor: Bestimmt über die Postleitzahl                                                                    *
 *********************************************************************************************************************
 */
@Service
public class CalculateService {

    private static final Logger logger = LoggerFactory.getLogger(CalculateService.class);
    private ErrorHandlingService errorHandlingService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    PostcodeRepository postcodeRepository;
    @Autowired
    private EntityService entityService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private AnnoKilometersRepository annoKilometersRepository;
    @Autowired
    private RegionRepository regionRepository;

    @Value("${insurance.premium.basis}")
    private int basis;

    /**
     *****************************************************************************************************************
     * Konstruktor für den CalculateService.                                                                          *
     *                                                                                                               *
     * @param regionRepository Repository für Regionen                                                               *
     * @param vehicleRepository Repository für Fahrzeuge                                                             *
     * @param annoKilometersRepository Repository für Jahreskilometerbereiche                                       *
     *****************************************************************************************************************
     */
    @Autowired
    public CalculateService(RegionRepository regionRepository,
                            VehicleRepository vehicleRepository,
                            AnnoKilometersRepository annoKilometersRepository) {
        this.regionRepository = regionRepository;
        this.vehicleRepository = vehicleRepository;
        this.annoKilometersRepository = annoKilometersRepository;
    }

    /**
     *****************************************************************************************************************
     * Berechnet den Regionsfaktor basierend auf der angegebenen Postleitzahl.                                        *
     *                                                                                                               *
     * @param postcode Die Postleitzahl zur Ermittlung des Regionsfaktors                                            *
     * @return Der berechnete Regionsfaktor                                                                         *
     *****************************************************************************************************************
     */
    public double calculateRegionFactor(String postcode) {
        return entityService.getRegionFactor(postcode);
    }

    /**
     *****************************************************************************************************************
     * Berechnet den Fahrzeugfaktor basierend auf der angegebenen Fahrzeug-ID.                                       *
     *                                                                                                               *
     * @param vehicleId Die ID des Fahrzeugs                                                                         *
     * @return Der berechnete Fahrzeugfaktor                                                                        *
     *****************************************************************************************************************
     */
    public double calculateVehicleFactor(Long vehicleId) {
        return entityService.getVehicleFactor(vehicleId);
    }

    /**
     *****************************************************************************************************************
     * Berechnet den Kilometerfaktor basierend auf dem angegebenen Kilometerbereich.                                 *
     *                                                                                                               *
     * @param kmMin Minimale Kilometeranzahl                                                                         *
     * @param kmMax Maximale Kilometeranzahl                                                                         *
     * @return Der Faktor für den angegebenen Kilometerbereich                                                       *
     * @throws RuntimeException Wenn kein passender Kilometerbereich gefunden wird                                  *
     *****************************************************************************************************************
     */
    public double calculateAnnoKilometersFactor(int kmMin, int kmMax) {
        var annoKilometers = annoKilometersRepository.findByMinLessThanEqualAndMaxGreaterThanEqual(kmMin, kmMax);

        if (!annoKilometers.isEmpty()) {
            return annoKilometers.get(0).getFactor();
        } else {
            logger.error("Kein Kilometerbereich für {} - {} km gefunden", kmMin, kmMax);
            throw new RuntimeException("Kein Kilometerbereich für " + kmMin + " - " + kmMax + " km gefunden.");
        }
    }

    /**
     *****************************************************************************************************************
     * Validiert die Eingaben für die Prämienberechnung und sammelt mögliche Fehlermeldungen.                        *
     *                                                                                                               *
     * @param premiumRequest Map mit den Eingabeparametern:                                                          *
     *                       - "vehicleId" (Long): ID des Fahrzeugs                                                  *
     *                       - "annoKilometers" (Integer): Jahreskilometerleistung                                   *
     *                       - "postcode" (String): Postleitzahl                                                     *
     * @param calculateService Instanz des CalculateService                                                          *
     * @return Liste von Fehlermeldungen (leer, wenn keine Fehler vorliegen)                                         *
     *****************************************************************************************************************
     */
    public List<String> validatePremiumRequest(Map<String, Object> premiumRequest, CalculateService calculateService) {
        var errorMessages = new ArrayList<String>();

        var vehicleIdNumber = (Number) premiumRequest.get("vehicleId");
        var vehicleId = vehicleIdNumber != null ? vehicleIdNumber.longValue() : null;
        var annoKilometers = (Integer) premiumRequest.get("annoKilometers");
        var postcode = (String) premiumRequest.get("postcode");

        if (vehicleId == null) errorMessages.add("vehicleId muss angegeben werden.");
        if (annoKilometers == null) errorMessages.add("annoKilometers muss angegeben werden.");
        if (postcode == null) errorMessages.add("postcode muss angegeben werden.");

        if (vehicleId != null && vehicleRepository.findById(vehicleId).isEmpty()) {
            errorMessages.add("Kein Fahrzeug mit der angegebenen ID gefunden.");
        }

        if (postcode != null && postcodeRepository.findFirstByPostcodeValue(postcode).isEmpty()) {
            errorMessages.add("Kein Postleitzahl-Eintrag für den angegebenen Wert gefunden.");
        }

        if (annoKilometers != null && annoKilometers <= 0) {
            errorMessages.add("Kilometerzahl muss größer als 0 sein.");
        }

        return errorMessages;
    }

    /**
     *****************************************************************************************************************
     * Berechnet die Versicherungsprämie basierend auf den angegebenen Parametern.                                   *
     *                                                                                                               *
     * @param vehicleId Die ID des Fahrzeugs                                                                         *
     * @param annoKilometers Jahreskilometerleistung                                                                 *
     * @param postcode Postleitzahl zur Ermittlung des Regionsfaktors                                                 *
     * @return Die berechnete Prämie                                                                                 *
     * @throws RuntimeException Bei Fehlern während der Berechnung                                                   *
     *****************************************************************************************************************
     */
    public double calculatePremium(Long vehicleId, Integer annoKilometers, String postcode) {
        try {
            var f1 = calculateAnnoKilometersFactor(annoKilometers, annoKilometers);
            var f2 = calculateVehicleFactor(vehicleId);
            var f3 = calculateRegionFactor(postcode);

            var premiumFactor = f1 * f2 * f3;
            return premiumFactor * basis;
        } catch (RuntimeException ex) {
            logger.error("Fehler bei der Berechnung der Prämie: {}", ex.getMessage());
            throw ex;
        }

    }

    /**
     *****************************************************************************************************************
     * Berechnet die Prämie und speichert die zugehörigen Statistikdaten.                                            *
     *                                                                                                               *
     * @param annoKilometers Jahreskilometerleistung                                                                 *
     * @param postcodeValue Postleitzahl                                                                             *
     * @param vehicleId Fahrzeug-ID                                                                                  *
     * @param ipAddress IP-Adresse des Anfragenden                                                                   *
     * @return Die berechnete Prämie                                                                                 *
     * @throws IllegalArgumentException Wenn ungültige Eingaben übergeben werden                                     *
     *****************************************************************************************************************
     */
    public double calculateAndSavePremiumWithStatistics(int annoKilometers, String postcodeValue, Long vehicleId, String ipAddress) {
        var postcode = entityService.findPostcode(postcodeValue)
                .orElseThrow(() -> new IllegalArgumentException("Ungültige Postleitzahl"));
        var city = entityService.findCityByPostcode(postcodeValue)
                .orElseThrow(() -> new IllegalArgumentException("Stadt nicht gefunden"));
        var region = entityService.findRegionByCityName(city.getName())
                .orElseThrow(() -> new IllegalArgumentException("Region nicht gefunden"));
        var vehicle = entityService.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Fahrzeug nicht gefunden"));

        var premium = calculatePremium(vehicleId, annoKilometers, postcodeValue);
        statisticsService.saveStatistics(LocalDateTime.now(), postcodeValue, vehicle.getName(), annoKilometers, premium, ipAddress);

        return premium;
    }

    /**
     *****************************************************************************************************************
     * Berechnet die Prämie und erstellt eine Antwort im Format einer PremiumResponse.                              *
     *                                                                                                               *
     * @param premiumRequest Map mit den erforderlichen Parametern:                                                  *
     *                       - "vehicleId" (Long): Fahrzeug-ID                                                       *
     *                       - "annoKilometers" (Integer): Jahreskilometerleistung                                   *
     *                       - "postcode" (String): Postleitzahl                                                     *
     * @return Ein PremiumResponse-Objekt mit der berechneten Prämie                                                 *
     *****************************************************************************************************************
     */
    public PremiumResponse calculateAndBuildResponse(Map<String, Object> premiumRequest) {
        var vehicleId = ((Number) premiumRequest.get("vehicleId")).longValue();
        var annoKilometers = (Integer) premiumRequest.get("annoKilometers");
        var postcode = (String) premiumRequest.get("postcode");

        var premiumAmount = calculatePremium(vehicleId, annoKilometers, postcode);

        var response = new PremiumResponse();
        response.setPremium(Collections.singletonMap("premium", premiumAmount));
        return response;
    }
}
