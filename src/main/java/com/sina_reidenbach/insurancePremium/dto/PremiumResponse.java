package com.sina_reidenbach.insurancePremium.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 *************************************************************************************************************
 * Dient als Datenübertragungsobjekt (DTO) für die Berechnungsergebnisse der Versicherungsprämie.            *
 * Enthält die berechnete Prämie und zugehörige Informationen in Form einer Map.                             *
 *************************************************************************************************************
 */
@Getter
@Setter
public class PremiumResponse {

    private Map<String, Object> premium;

    public PremiumResponse() {}

    /**
     *********************************************************************************************************
     * Konstruktor, der eine Map mit den Berechnungsergebnissen übergibt.                                   *
     *
     * @param premium Die Map, die die berechneten Prämieninformationen enthält.                             *
     *********************************************************************************************************
     */
    public PremiumResponse(Map<String, Object> premium) {
        this.premium = premium;
    }
}
