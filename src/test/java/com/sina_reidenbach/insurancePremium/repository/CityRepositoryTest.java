package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNull;


public class CityRepositoryTest {
    @Mock
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        City city = new City("Leverkusen");

        when(cityRepository.findFirstByName("Leverkusen")).thenReturn(Optional.of(city));
        when(cityRepository.findByPostcodes_PostcodeValue("51373")).thenReturn(city);
    }

    @Test
    void testFindFirstByName(){
        Optional<City> city = cityRepository.findFirstByName("Leverkusen");
        assertTrue(city.isPresent());

        City result = city.get();

        assertEquals("Leverkusen", result.getName());
    }


    @Test
    void testFindByPostcodes_PostcodeValue(){
        City result = cityRepository.findByPostcodes_PostcodeValue("51373");

        assertEquals("Leverkusen", result.getName());
    }
    @Test
    void testFindFirstByName_NotFound() {
        Optional<City> city = cityRepository.findFirstByName("NichtExistierendeStadt");
        assertTrue(city.isEmpty());
    }

    @Test
    void testFindByPostcodes_PostcodeValue_NotFound() {
        City result = cityRepository.findByPostcodes_PostcodeValue("00000");
        assertNull(result);
    }
    @Test
    void testFindFirstByName_EmptyString() {
        Optional<City> city = cityRepository.findFirstByName("");
        assertTrue(city.isEmpty());
    }

    @Test
    void testFindByPostcodes_PostcodeValue_EmptyString() {
        City result = cityRepository.findByPostcodes_PostcodeValue("");
        assertNull(result);
    }
}

