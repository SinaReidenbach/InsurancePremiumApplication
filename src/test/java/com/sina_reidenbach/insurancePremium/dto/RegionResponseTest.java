package com.sina_reidenbach.insurancePremium.dto;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RegionResponseTest {

    @Test
    void testSetterAndGetter() {
        RegionResponse response = new RegionResponse();
        Map<Long, Map<String, Object>> testRegions = Map.of(
                1L, Map.of("name", "Region A", "factor", 1.2),
                2L, Map.of("name", "Region B", "factor", 1.5)
        );

        response.setRegions(testRegions);

        assertNotNull(response.getRegions());
        assertEquals(2, response.getRegions().size());
        assertEquals("Region A", response.getRegions().get(1L).get("name"));
        assertEquals(1.5, response.getRegions().get(2L).get("factor"));
    }
}