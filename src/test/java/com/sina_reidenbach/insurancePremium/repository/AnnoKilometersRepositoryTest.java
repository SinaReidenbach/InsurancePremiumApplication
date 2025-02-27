package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Anno_Kilometers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AnnoKilometersRepositoryTest {
    @Mock
    private AnnoKilometersRepository annoKilometersRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Anno_Kilometers annoKilometers = new Anno_Kilometers(0,1000,0.5);

        when(annoKilometersRepository.findById(1L)).thenReturn(Optional.of(annoKilometers));

    }

    @Test
    void testFindByKmMinLessThanEqualAndKmMaxGreaterThanEqual() {
        Anno_Kilometers annoKilometers1 = new Anno_Kilometers(0, 5000, 0.5);
        Anno_Kilometers annoKilometers2 = new Anno_Kilometers(5001, 10000, 1.0);
        Anno_Kilometers annoKilometers3 = new Anno_Kilometers(10001, 20000, 1.5);
        Anno_Kilometers annoKilometers4 = new Anno_Kilometers(20001, 99999, 2.0);

        for (int i = 0; i < 30000; i += 5000) {
            Anno_Kilometers expectedAnnoKilometers;
            if (i >= 0 && i <= 5000) {
                expectedAnnoKilometers = annoKilometers1;
            } else if (i > 5000 && i <= 10000) {
                expectedAnnoKilometers = annoKilometers2;
            } else if (i > 10000 && i <= 20000) {
                expectedAnnoKilometers = annoKilometers3;
            } else {
                expectedAnnoKilometers = annoKilometers4;
            }

            when(annoKilometersRepository.findByMinLessThanEqualAndMaxGreaterThanEqual(i, i))
                    .thenReturn(List.of(expectedAnnoKilometers));

            List<Anno_Kilometers> result = annoKilometersRepository.findByMinLessThanEqualAndMaxGreaterThanEqual(i, i);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedAnnoKilometers.getFactor(), result.get(0).getFactor());
        }
    }
    @Test
    void testFindById() {

        Optional<Anno_Kilometers> annoKilometers = annoKilometersRepository.findById(1L);
        assertTrue(annoKilometers.isPresent());

        Anno_Kilometers result = annoKilometers.get();

        assertEquals(0.5, result.getFactor());

    }
    @Test
    void testFindById_notFound() {
        when(annoKilometersRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Anno_Kilometers> result = annoKilometersRepository.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByMinLessThanEqualAndMaxGreaterThanEqual_noMatch() {
        List<Anno_Kilometers> result = annoKilometersRepository.findByMinLessThanEqualAndMaxGreaterThanEqual(100000, 200000);
        assertTrue(result.isEmpty());
    }
}