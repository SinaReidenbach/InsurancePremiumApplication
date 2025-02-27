package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Region;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RegionRepositoryTest {
    private AutoCloseable closeable;
    @Mock
    private RegionRepository regionRepository;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Region region = new Region("Nordrhein-Westfalen",1.5);
        when(regionRepository.findByName("Nordrhein-Westfalen")).thenReturn(Optional.of(region));
        when(regionRepository.findByCities_Name("Leverkusen")).thenReturn(region);
        when(regionRepository.findByPostcodeValueStartingWith("51373")).thenReturn(Optional.of(region));

    }
    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testFindByRegionName() {

        Optional<Region> region = regionRepository.findByName("Nordrhein-Westfalen");
        assertTrue(region.isPresent());

        Region result = region.get();


        assertEquals("Nordrhein-Westfalen", result.getName().strip());
        assertEquals(1.5, result.getFactor());
    }

    @Test
    void testFindByRegionNameNotFound() {
        Optional<Region> region = regionRepository.findByName("Bayern");
        assertFalse(region.isPresent());
    }

    @Test
    void testfindByCities_Name() {

        Region result = regionRepository.findByCities_Name("Leverkusen");

        assertEquals("Nordrhein-Westfalen", result.getName());
        assertEquals(1.5, result.getFactor());
    }

    @Test
    void testFindByCities_NameNotFound() {
        Region result = regionRepository.findByCities_Name("München");
        assertNull(result);
    }

    @Test
    void testfindByPostcodeValueStartingWith() {

        Optional<Region> region = regionRepository.findByPostcodeValueStartingWith("51373");
        assertTrue(region.isPresent());

        Region result = region.get();

        assertEquals("Nordrhein-Westfalen", result.getName());
        assertEquals(1.5, result.getFactor());
    }

    @Test
    void testFindByPostcodeValueStartingWithNotFound() {
        Optional<Region> region = regionRepository.findByPostcodeValueStartingWith("99999");
        assertFalse(region.isPresent());
    }

}
