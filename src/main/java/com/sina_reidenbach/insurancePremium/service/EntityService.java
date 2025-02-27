package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.dto.AnnoKilometersResponse;
import com.sina_reidenbach.insurancePremium.dto.PremiumResult;
import com.sina_reidenbach.insurancePremium.dto.RegionResponse;
import com.sina_reidenbach.insurancePremium.dto.VehicleResponse;
import com.sina_reidenbach.insurancePremium.model.City;
import com.sina_reidenbach.insurancePremium.model.Postcode;
import com.sina_reidenbach.insurancePremium.model.Region;
import com.sina_reidenbach.insurancePremium.model.Vehicle;
import com.sina_reidenbach.insurancePremium.repository.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 *************************************************************************************************************
 * Verantwortlich für die Steuerung von Entitäten und deren Interaktionen im Backend.                        *
 * Handhabt die Bereitstellung von Fahrzeugen, Regionen und Postleitzahlen sowie das Filtern und Sortieren.  *
 *************************************************************************************************************
 */
@Getter
@Setter
@Service
public class EntityService {

    @Autowired
    ErrorHandlingService errorHandlingService;
    @Autowired
    private PostcodeRepository postcodeRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private AnnoKilometersRepository annoKilometersRepository;

    /**
     ********************************************************************************************************
     * Konstruktor                                                                                          *
     *                                                                                                      *
     * @param postcodeRepository Repository für Postleitzahlen.                                             *
     * @param cityRepository Repository für Städte.                                                         *
     * @param regionRepository Repository für Regionen.                                                     *
     ********************************************************************************************************
     */
    @Autowired
    public EntityService(PostcodeRepository postcodeRepository,
                         CityRepository cityRepository,
                         RegionRepository regionRepository) {
        this.postcodeRepository = postcodeRepository;
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
    }

    /**
     *********************************************************************************************************
     * Gibt eine sortierte Liste von Fahrzeugen zurück.                                                      *
     *                                                                                                       *
     * @return Eine Liste von Fahrzeugen, sortiert nach ihrem Namen.                                         *
     *********************************************************************************************************
     */
    public List<Vehicle> getSortedVehicleList() {
        var vehicleList = vehicleRepository.findAll();
        vehicleList.sort(Comparator.comparing(Vehicle::getName));
        return vehicleList;
    }

    /**
     ********************************************************************************************************
     * Sucht ein Fahrzeug anhand seiner ID.                                                                 *
     *                                                                                                      *
     * @param id Die ID des Fahrzeugs.                                                                      *
     * @return Ein Optional mit dem Fahrzeug, wenn gefunden.                                                *
     ********************************************************************************************************
     */
    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    /**
     ********************************************************************************************************
     * Gibt den Faktor für ein Fahrzeug anhand seiner ID zurück.                                            *
     *                                                                                                      *
     * @param vehicleId Die ID des Fahrzeugs.                                                               *
     * @return Der Faktor des Fahrzeugs.                                                                    *
     * @throws RuntimeException Wenn das Fahrzeug nicht gefunden wurde.                                     *
     ********************************************************************************************************
     */
    public double getVehicleFactor(Long vehicleId) {
        var vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isPresent()) {
            return vehicleOpt.get().getFactor();
        } else {
            errorHandlingService.logAndThrowError("Fahrzeug mit ID " + vehicleId + " nicht gefunden.");
        }
        return 0;
    }

    /**
     ********************************************************************************************************
     * Filtert die Postleitzahlen anhand eines Eingabewerts.                                                *
     *                                                                                                      *
     * @param input Der Eingabewert zur Filterung der Postleitzahlen.                                       *
     * @return Eine Liste von Postleitzahlen, die mit dem Eingabewert beginnen.                             *
     ********************************************************************************************************
     */
    public List<Postcode> filterPostcodesByInput(String input) {
        return postcodeRepository.findAll().stream()
                .filter(postcode -> postcode.getPostcodeValue().startsWith(input))
                .toList();
    }

    /**
     ********************************************************************************************************
     * Gibt eine sortierte Liste der Postleitzahlen zurück.                                                 *
     *                                                                                                      *
     * @return Eine sortierte Liste von Postleitzahlen.                                                     *
     ********************************************************************************************************
     */
    public List<Postcode> getSortedPostcodes() {
        return postcodeRepository.findAll().stream()
                .sorted(Comparator.comparing(Postcode::getPostcodeValue))
                .collect(Collectors.toList());
    }

    /**
     ********************************************************************************************************
     * Sucht eine Postleitzahl anhand ihres Werts.                                                          *
     *                                                                                                      *
     * @param postcodeValue Der Wert der Postleitzahl, nach der gesucht werden soll.                        *
     * @return Ein Optional mit der Postleitzahl, wenn gefunden.                                            *
     ********************************************************************************************************
     */
    public Optional<Postcode> findPostcode(String postcodeValue) {
        return postcodeRepository.findFirstByPostcodeValue(postcodeValue);
    }

    /**
     ********************************************************************************************************
     * Prüft, ob eine bestimmte Postleitzahl existiert.                                                     *
     *                                                                                                      *
     * @param postcode Die Postleitzahl, die überprüft werden soll.                                         *
     * @return True, wenn die Postleitzahl existiert, andernfalls false.                                    *
     ********************************************************************************************************
     */
    public boolean isPostcodeValid(String postcode) {
        return postcodeRepository.existsByPostcodeValue(postcode);
    }

    /**
     ********************************************************************************************************
     * Generiert eine HTML-Liste von Postleitzahlen basierend auf einer Eingabe.                            *
     *                                                                                                      *
     * @param input Der Eingabewert zur Filterung der Postleitzahlen.                                       *
     * @return Eine HTML-Option-Liste mit Postleitzahlen.                                                   *
     ********************************************************************************************************
     */

    public String generatePostcodeOptions(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "<option value=\"\">Keine Postleitzahl eingegeben</option>";
        }
        var filteredPostcodes = filterPostcodesByInput(input);
        if (filteredPostcodes.isEmpty()) {
            return "<option value=\"\">Keine Postleitzahlen gefunden</option>";
        }

        return filteredPostcodes.stream()
                .map(postcode -> String.format("<option value=\"%s\">%s</option>", postcode.getPostcodeValue(), postcode.getPostcodeValue()))
                .collect(Collectors.joining());
    }

    /**
     ********************************************************************************************************
     * Sucht eine Stadt anhand ihrer Postleitzahl.                                                          *
     *                                                                                                      *
     * @param postcodeValue Der Wert der Postleitzahl, nach der die Stadt gesucht werden soll.              *
     * @return Ein Optional mit der Stadt, wenn gefunden.                                                   *
     ********************************************************************************************************
     */
    public Optional<City> findCityByPostcode(String postcodeValue) {
        return Optional.ofNullable(cityRepository.findByPostcodes_PostcodeValue(postcodeValue));
    }


    /**
     ********************************************************************************************************
     * Sucht eine Region anhand des Namens einer Stadt.                                                     *
     *                                                                                                      *
     * @param cityName Der Name der Stadt, nach der die Region gesucht werden soll.                         *
     * @return Ein Optional mit der Region, wenn gefunden.                                                  *
     ********************************************************************************************************
     */
    public Optional<Region> findRegionByCityName(String cityName) {
        return Optional.ofNullable(regionRepository.findByCities_Name(cityName));
    }

    /**
     ********************************************************************************************************
     * Gibt den Namen der Region für eine bestimmte Postleitzahl zurück.                                    *
     *                                                                                                      *
     * @param postcodeValue Der Wert der Postleitzahl, für die der Region-Name gesucht werden soll.         *
     * @return Der Name der Region, oder "Unbekannt", wenn keine Region gefunden wurde.                     *
     ********************************************************************************************************
     */
    public String getRegionNameByPostcode(String postcodeValue) {
        return findRegionByCityName(postcodeValue).map(Region::getName).orElse("Unbekannt");
    }

    /**
     ********************************************************************************************************
     * Gibt den Faktor der Region für eine bestimmte Postleitzahl zurück.                                   *
     *                                                                                                      *
     * @param postcode Die Postleitzahl, für die der Region-Faktor gesucht wird.                            *
     * @return Der Faktor der Region, wenn gefunden.                                                        *
     * @throws RuntimeException Wenn die Region für die Postleitzahl nicht gefunden wurde.                  *
     ********************************************************************************************************
     */
    public double getRegionFactor(String postcode) {
        var regionOpt = regionRepository.findByPostcodeValueStartingWith(postcode);
        if (regionOpt.isPresent()) {
            return regionOpt.get().getFactor();
        } else {
            errorHandlingService.logAndThrowError("Region für Postleitzahl " + postcode + " nicht gefunden.");
        }
        return 0;
    }

    /**
     ********************************************************************************************************
     * Fügt das Premium-Ergebnis und die Region zu den Model-Attributen hinzu.                              *
     * Die Prämie wird dabei im deutschen Zahlenformat mit zwei Dezimalstellen und Komma als Trennzeichen  *
     * formatiert. Ein Euro-Zeichen wird an die formatierte Prämie angehängt.                              *
     *                                                                                                      *
     * @param model Das Model, dem die Attribute hinzugefügt werden.                                        *
     * @param result Das PremiumResult, das die berechnete Prämie und Region enthält.                       *
     ********************************************************************************************************
     */
    public void addPremiumToModel(Model model, PremiumResult result) {
        DecimalFormat df = new DecimalFormat("###,##0.00", new DecimalFormatSymbols(Locale.GERMANY));
        model.addAttribute("premium", df.format(result.getPremium()) + " €");
        model.addAttribute("region", result.getRegion());
    }

    /**
     ********************************************************************************************************
     * Fügt die Standard-Model-Attribute für Fahrzeuge und Postleitzahlen hinzu.                            *
     *                                                                                                      *
     * @param model Das Model, dem die Standard-Attribute hinzugefügt werden.                               *
     ********************************************************************************************************
     */
    public void addDefaultAttributes(Model model) {
        model.addAttribute("vehicleList", getSortedVehicleList());
        model.addAttribute("postcodeList", getSortedPostcodes());
    }

    /**
     ********************************************************************************************************
     * Gibt eine Antwort mit allen Fahrzeugen zurück.                                                       *
     *                                                                                                      *
     * @return Eine VehicleResponse, die eine Liste aller Fahrzeuge enthält.                                *
     ********************************************************************************************************
     */
    public VehicleResponse getVehicles() {
        var response = new VehicleResponse();
        var vehicles = vehicleRepository.findAll();

        if (vehicles.isEmpty()) {
            response.setVehicles(Map.of(0L, Map.of(
                    "error", "NO_VEHICLES_FOUND",
                    "message", "Keine Fahrzeuge gefunden."
            )));
            return response;
        }

        var vehicleMap = new HashMap<Long, Map<String, Object>>();
        vehicles.forEach(vehicle -> vehicleMap.put(vehicle.getId(), Map.of(
                "vehicleName", vehicle.getName(),
                "factor", vehicle.getFactor()
        )));
        response.setVehicles(vehicleMap);
        return response;
    }

    /**
     ********************************************************************************************************
     * Gibt eine Antwort mit allen Regionen zurück.                                                         *
     *                                                                                                      *
     * @return Eine RegionResponse, die eine Liste aller Regionen enthält.                                  *
     ********************************************************************************************************
     */
    public RegionResponse getRegions() {
        var response = new RegionResponse();
        var regionMap = new HashMap<Long, Map<String, Object>>();
        regionRepository.findAll().forEach(region -> regionMap.put(region.getId(), Map.of(
                "regionName", region.getName(),
                "factor", region.getFactor()
        )));
        response.setRegions(regionMap);
        return response;
    }

    /**
     *******************************************************************************************************
     * Gibt eine Antwort mit allen AnnoKilometers-Daten zurück.                                            *
     *                                                                                                     *
     * @return Eine AnnoKilometersResponse, die eine Liste aller AnnoKilometers-Daten enthält.             *
     *******************************************************************************************************
     */
    public AnnoKilometersResponse getAnnoKilometers() {
        var response = new AnnoKilometersResponse();
        var kmList = new ArrayList<Map<String, Object>>();
        annoKilometersRepository.findAll().forEach(km -> kmList.add(Map.of(
                "min", km.getMin(),
                "max", km.getMax(),
                "factor", km.getFactor()
        )));
        response.setAnnoKilometers(kmList);
        return response;
    }
}
