package com.sina_reidenbach.insurancePremium.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 *************************************************************************************************************
 * Dient als Datenübertragungsobjekt (DTO) für Fahrzeuginformationen.                                        *
 * Enthält eine Map mit Fahrzeugen, die über ihre IDs zugeordnet sind.                                       *
 *************************************************************************************************************
 */
@Getter
@Setter
public class VehicleResponse {

    private Map<Long, Map<String, Object>> vehicles;
}
