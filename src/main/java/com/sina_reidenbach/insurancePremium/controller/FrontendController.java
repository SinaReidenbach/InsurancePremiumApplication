package com.sina_reidenbach.insurancePremium.controller;

import com.sina_reidenbach.insurancePremium.dto.PremiumResult;
import com.sina_reidenbach.insurancePremium.service.*;
import com.sina_reidenbach.insurancePremium.utils.IpUtils;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 ***************************************************************************************************************
 * Verantwortlich für die Steuerung des Frontends.                                                             *
 * Handhabt Anfragen für die Startseite, Berechnungen von Versicherungsprämien und Postleitzahl-Filterungen.   *
 ***************************************************************************************************************
 */
@Controller
@Hidden
public class FrontendController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);

    @Autowired
    private ErrorHandlingService errorHandlingService;

    private PremiumResult premiumResult;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CalculateService calculateService;

    /**
     ***************************************************************************************************************
     * Zeigt die Startseite an und fügt Standardattribute zum Model hinzu.                                         *
     *                                                                                                             *
     * @param input Optionaler Parameter für Benutzereingaben auf der Startseite.                                  *
     * @param model Das Model, dem die Attribute hinzugefügt werden.                                               *
     * @return Der Name der View, die angezeigt werden soll.                                                       *
     ***************************************************************************************************************
     */
    @GetMapping("/")
    public String showHomePage(@RequestParam(required = false) String input, Model model) {
        entityService.addDefaultAttributes(model);
            return "index";
    }

    /**
     ***************************************************************************************************************
     * Filtert Postleitzahlen basierend auf Benutzereingaben und liefert passende Optionen.                        *
     *                                                                                                             *
     * @param input Die Postleitzahl, nach der gefiltert werden soll.                                              *
     * @return Eine JSON-Antwort mit den gefilterten Postleitzahlen-Optionen.                                      *
     ***************************************************************************************************************
     */
    @GetMapping("/filter-postcodes")
    @ResponseBody
    public String filterPostcodes(@RequestParam String input) {
        return entityService.generatePostcodeOptions(input);
    }

    /**
     * **************************************************************************************************************
     * Berechnet die Versicherungsprämie basierend auf Benutzereingaben.                                            *
     * Fügt Ergebnisse und Fahrzeugliste dem Model hinzu und behandelt mögliche Eingabefehler.                      *
     *                                                                                                              *
     * @param km Die Anzahl der Kilometer, die das Fahrzeug jährlich fährt.                                         *
     * @param postcodeValue Die Postleitzahl des Benutzers.                                                         *
     * @param vehicleId Die ID des Fahrzeugs.                                                                       *
     * @param request Das HttpServletRequest-Objekt, um die IP-Adresse des Benutzers zu erhalten.                   *
     * @param model Das Model, dem die berechneten Werte und Ergebnisse hinzugefügt werden.                         *
     * @return Der Name der View, die angezeigt werden soll.                                                        *
     ****************************************************************************************************************
     */
    @PostMapping("/berechnen")
    public String berechnen(@RequestParam int km,
                            @RequestParam String postcodeValue,
                            @RequestParam Long vehicleId,
                            HttpServletRequest request,
                            Model model) {
        try {
            if (!entityService.isPostcodeValid(postcodeValue)) {
                throw new IllegalArgumentException("Ungültige Postleitzahl");
            }

            var premium = calculateService.calculateAndSavePremiumWithStatistics(km, postcodeValue, vehicleId, IpUtils.getClientIp(request));

            var region = entityService.getRegionNameByPostcode(postcodeValue);
            this.premiumResult = new PremiumResult(premium, region);

            entityService.addPremiumToModel(model, premiumResult);
            model.addAttribute("vehicleList", entityService.getSortedVehicleList());

            return "index";

        } catch (IllegalArgumentException e) {
            logger.error("Error occurred: {}", e.getMessage());
            errorHandlingService.handleError(model, e.getMessage());

            model.addAttribute("km", km);
            model.addAttribute("postcodeValue", postcodeValue);
            model.addAttribute("vehicleId", vehicleId);
            model.addAttribute("vehicleList", entityService.getSortedVehicleList());
            model.addAttribute("postcodeList", entityService.getSortedPostcodes());

            return "index";
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            errorHandlingService.handleError(model, "Ein unerwarteter Fehler ist aufgetreten.");
            return "index";
        }
    }
}