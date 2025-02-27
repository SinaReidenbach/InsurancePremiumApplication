package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.dto.ErrorResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 *************************************************************************************************************
 * Verantwortlich für das Fehlerhandling und das Logging.                                                    *
 * Handhabt Fehler durch Loggen von Nachrichten und Werfen von Exceptions.                                   *
 *************************************************************************************************************
 */
@Service
public class ErrorHandlingService {
    private final Logger logger = LoggerFactory.getLogger(ErrorHandlingService.class);

    /**
     *********************************************************************************************************
     * Behandelt Fehler, indem eine Fehlermeldung ins Model hinzugefügt und in den Logs protokolliert wird.  *
     * @param model Das Model, in das die Fehlermeldung eingefügt wird.                                      *
     * @param message Die Fehlermeldung, die angezeigt und geloggt werden soll.                              *
     * @return Gibt den Namen der View zurück, die angezeigt wird (in diesem Fall "index").                  *
     *********************************************************************************************************
     */
    public String handleError(Model model, String message) {
        model.addAttribute("error", message);
        logger.warn("[WARNUNG] {}", message);
        return "index";
    }


    /**
     *********************************************************************************************************
     * Loggt eine Fehlernachricht und wirft eine RuntimeException.                                           *
     * @param message Die Fehlermeldung, die geloggt und geworfen werden soll.                               *
     *********************************************************************************************************
     */
    public void logAndThrowError(String message) {
        logger.error(message);
        throw new RuntimeException(message);
    }


    /**
     *********************************************************************************************************
     * Behandelt Validierungsfehler, durch Rückgabe einer BadRequest-Antwort mit den Fehlernachrichten.      *
     * @param errorMessages Eine Liste von Fehlernachrichten, die den Validierungsfehler beschreiben.        *
     * @return Gibt eine ResponseEntity mit dem Statuscode 400 und einer ErrorResponse zurück.               *
     *********************************************************************************************************
     */
    public ResponseEntity<ErrorResponse> handleValidationErrors(List<String> errorMessages) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Fehlerhafte Eingabewerte", String.join(", ", errorMessages)));
    }


    /**
     *********************************************************************************************************
     * Behandelt Serverfehler, indem eine ResponseEntity mit einer 500-Fehlerantwort zurückgegeben wird.     *
     * @param e Die Exception, die den Serverfehler beschreibt.                                              *
     * @return Gibt eine ResponseEntity mit dem Statuscode 500 und einer ErrorResponse zurück.               *
     *********************************************************************************************************
     */
    public ResponseEntity<ErrorResponse> handleServerError(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("Serverfehler", "Ein unerwarteter Fehler ist aufgetreten"));
    }
}
