package com.sina_reidenbach.insurancePremium.controller;

import com.sina_reidenbach.insurancePremium.dto.*;
import com.sina_reidenbach.insurancePremium.service.CalculateService;
import com.sina_reidenbach.insurancePremium.service.EntityService;
import com.sina_reidenbach.insurancePremium.service.ErrorHandlingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

/**
 ****************************************************************************************************************
 * Verantwortlich für die Bereitstellung von API-Endpunkten für Drittanbieter.                                  *
 * Stellt Fahrzeug-, Regions- und Kilometeroptionen bereit und führt Prämienberechnungen durch.                 *
 ****************************************************************************************************************
 */
@RestController
public class ThirdPartyController {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyController.class);

    @Autowired
    private CalculateService calculateService;

    @Autowired
    private ErrorHandlingService errorHandlingService;

    @Autowired
    private EntityService entityService;

    /**
     ****************************************************************************************************************
     * Liefert eine Liste der verfügbaren Fahrzeugtypen mit den zugehörigen Prämienfaktoren.                        *
     *                                                                                                              *
     * @return Eine ResponseEntity mit einer Liste der verfügbaren Fahrzeugtypen und den zugehörigen Prämienfaktoren*
     ****************************************************************************************************************
     */
    @Operation(tags = "Fahrzeugtypen", description = "Gibt mögliche Optionen für die Fahrzeugtypen aus und die entsprechenden Prämien Faktoren")
    @GetMapping("/api/options/vehicles")
    public ResponseEntity<VehicleResponse> getVehicles() {
        return ResponseEntity.ok(entityService.getVehicles());
    }

    /**
     ****************************************************************************************************************
     * Gibt die verfügbaren Regionen für die Zulassungsstelle zurück.                                               *
     *                                                                                                              *
     * @return Eine ResponseEntity mit einer Liste der verfügbaren Regionen für die Zulassungsstelle.               *
     ****************************************************************************************************************
     */
    @Operation(tags = "Ansässigkeit Zulassungsstelle", description = "Gibt mögliche Optionen für die Regionen aus")
    @GetMapping("/api/options/regions")
    public ResponseEntity<RegionResponse> getRegions() {
        return ResponseEntity.ok(entityService.getRegions());
    }

    /**
     ****************************************************************************************************************
     * Gibt eine Übersicht über verfügbare Kilometerbereiche und die entsprechenden Faktoren.                       *
     *                                                                                                              *
     * @return Eine ResponseEntity mit einer Liste der verfügbaren Kilometerbereiche und den zugehörigen Faktoren.  *
     ****************************************************************************************************************
     */
    @Operation(summary = "Gibt die Optionen für die Kilometer Ranges aus")
    @GetMapping("/api/options/annoKilometers")
    public ResponseEntity<AnnoKilometersResponse> getAnnoKilometers() {
        return ResponseEntity.ok(entityService.getAnnoKilometers());
    }

    /**
     ****************************************************************************************************************
     * Berechnet die Versicherungsprämie basierend auf Fahrzeug, Postleitzahl und Jahreskilometern.                 *
     * Validiert die Eingaben und behandelt Berechnungs- sowie Serverfehler.                                        *
     *                                                                                                              *
     * @param premiumRequest Ein Map-Objekt, das die Eingabedaten für die Berechnung der Prämie enthält.            *
     * @return Eine ResponseEntity mit der berechneten Prämie oder eine Fehlerantwort, bei ungültigen Eingaben.     *
     ****************************************************************************************************************
     */

    @Operation(
            summary = "Berechnet die Prämie mit den entsprechenden Optionen",
            tags = "Prämienberechnung",
            description = "Berechnet die Prämie über die gesendeten Parameter Vehicle, Postcode und annoKilometers",
            responses = {
                    @ApiResponse(
                            description = "Prämienberechnung erfolgt und zurückgesendet",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Beispiel",
                                                    value = "{\"vehicleTypeId\": 14, \"postcode\": \"51373\", \"annoKilometers\": 2000}"
                                            )
                                    },
                                    schema = @Schema(implementation = PremiumResponse.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Ungültige Eingabedaten, wie z.B. fehlende oder falsche Werte",
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Beispiel 1: Ungültiger Fahrzeugtyp",
                                                    value = "{\"error\": \"Fehlerhafte Eingabewerte\", \"message\": \"Kein Fahrzeug mit der angegebenen ID gefunden.\"}",
                                                    summary = "Der angegebene Fahrzeugtyp existiert nicht"
                                            ),
                                            @ExampleObject(
                                                    name = "Beispiel 2: Ungültige Postleitzahl",
                                                    value = "{\"error\": \"Fehlerhafte Eingabewerte\", \"message\": \"Kein Postleitzahl-Eintrag für den angegebenen Wert gefunden.\"}",
                                                    summary = "Die angegebene Postleitzahl existiert nicht"
                                            ),
                                            @ExampleObject(
                                                    name = "Beispiel 3: Ungültige Jahreskilometerleistung",
                                                    value = "{\"error\": \"Fehlerhafte Eingabewerte\", \"message\": \"Kilometerzahl muss größer als 0 sein.\"}",
                                                    summary = "Die angegebene Jahreskilometerleistung liegt im Bereich des unmöglichen"
                                            )
                                    },
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Serverfehler bei der Berechnung der Prämie",
                            responseCode = "500",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Serverfehler",
                                                    value = "{\"error\": \"Serverfehler\", \"message\": \"Ein unerwarteter Fehler ist aufgetreten\"}",
                                                    summary = "Interner Fehler bei der Berechnung der Prämie"
                                            )
                                    },
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/api/calculate")
    public ResponseEntity<?> calculatePremium(@RequestBody Map<String, Object> premiumRequest) {
        var errorMessages = calculateService.validatePremiumRequest(premiumRequest, calculateService);

        if (!errorMessages.isEmpty()) {
            return errorHandlingService.handleValidationErrors(errorMessages);
        }
        try {
            var response = calculateService.calculateAndBuildResponse(premiumRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Fehler bei der Prämienberechnung: {}", e.getMessage());
            return errorHandlingService.handleServerError(e);
        }
    }
}
