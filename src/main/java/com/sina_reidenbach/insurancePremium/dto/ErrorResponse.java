package com.sina_reidenbach.insurancePremium.dto;

import lombok.Getter;
import lombok.Setter;

/**
 ****************************************************************************************************************
 * Dient als Datenübertragungsobjekt (DTO) für Fehlermeldungen.                                                 *
 * Enthält Informationen über den Fehlertyp und eine entsprechende Nachricht.                                   *
 ****************************************************************************************************************
 */
@Getter
@Setter
public class ErrorResponse {

    private String error;
    private String message;


    /**
     *************************************************************************************************************
     * Konstruktor für die Erstellung einer Fehlermeldung.                                                       *
     *                                                                                                           *
     * @param error   Der Fehlertyp, der die Art des Fehlers beschreibt.                                         *
     * @param message Eine detaillierte Nachricht, die den Fehler weiter erklärt.                                *
     *************************************************************************************************************
     */
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
