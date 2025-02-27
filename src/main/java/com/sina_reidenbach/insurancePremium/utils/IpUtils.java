package com.sina_reidenbach.insurancePremium.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *************************************************************************************************************
 * Verantwortlich für die Verarbeitung und Extraktion der Client-IP-Adresse aus der Anfrage.                 *
 * Stellt eine Methode zur Verfügung, um die IP-Adresse des Clients zu ermitteln, auch bei Nutzung von       *
 * Proxy-Servern oder Load-Balancern.                                                                        *
 *************************************************************************************************************
 */
public class IpUtils {

    /**
     *********************************************************************************************************
     * Gibt die IP-Adresse des Clients zurück.                                                               *
     * Wenn die Anfrage über einen Proxy oder Load-Balancer kommt, wird die IP aus dem "X-Forwarded-For"     *
     * Header extrahiert. Ansonsten wird die Remote-IP aus der Anfrage verwendet.                            *
     *********************************************************************************************************
     */
    public static String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
