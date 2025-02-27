package com.sina_reidenbach.insurancePremium.dto;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PremiumResponseTest {

    @Test
    void testConstructorWithMap() {
        Map<String, Object> premiumData = Map.of("amount", 100.50, "currency", "EUR");

        PremiumResponse response = new PremiumResponse(premiumData);

        assertNotNull(response.getPremium());
        assertEquals(100.50, response.getPremium().get("amount"));
        assertEquals("EUR", response.getPremium().get("currency"));
    }
}
