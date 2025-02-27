package com.sina_reidenbach.insurancePremium.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 *************************************************************************************************************
 * Dient als Datenübertragungsobjekt (DTO) für Regionsinformationen.                                         *
 * Enthält eine Map mit Regionen, die über ihre IDs zugeordnet sind.                                         *
 *************************************************************************************************************
 */
@Getter
@Setter
public class RegionResponse {

    private Map<Long, Map<String, Object>> regions;
}
