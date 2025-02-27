package com.sina_reidenbach.insurancePremium.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;


/**
 ***************************************************************************************************************
 * Dient als Datenübertragungsobjekt (DTO) für die Kilometerbereichsoptionen.                                  *
 * Enthält eine Liste von Jahreskilometerbereichen für die Prämienberechnung.                                  *
 ***************************************************************************************************************
 */
@Getter
@Setter
public class AnnoKilometersResponse {

    private List<Map<String, Object>> annoKilometers;
}
