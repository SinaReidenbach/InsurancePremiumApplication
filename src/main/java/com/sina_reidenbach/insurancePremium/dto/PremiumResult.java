package com.sina_reidenbach.insurancePremium.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *************************************************************************************************************
 * Dient als Datenübertragungsobjekt (DTO) für das Ergebnis der Prämienberechnung.                           *
 * Enthält die berechnete Versicherungsprämie und die zugehörige Region.                                     *
 *************************************************************************************************************
 */
@Getter
@Setter
public class PremiumResult {

    private final double premium;
    private final String region;

    /**
     *********************************************************************************************************
     * Konstruktor für das PremiumResult DTO.                                                                  *
     *
     * @param premium Die berechnete Versicherungsprämie.                                                      *
     * @param region Die Region, die der berechneten Prämie zugeordnet ist.                                    *
     *********************************************************************************************************
     */
    public PremiumResult(double premium, String region) {
        this.premium = premium;
        this.region = region;
    }
}
