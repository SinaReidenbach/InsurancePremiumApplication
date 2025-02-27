package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Postcode;
import com.sina_reidenbach.insurancePremium.model.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class PostcodeRepositoryTest {
    @Mock
    private PostcodeRepository postcodeRepository;

    private Postcode postcode1;
    private Postcode postcode2;
    private Postcode postcode3;
    private Region region;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        region = new Region();
        region.setName("Nordrheinwestfalen");

        postcode1 = new Postcode();
        postcode1.setPostcodeValue("51373");
        postcode1.setRegion(region);

        postcode2 = new Postcode();
        postcode2.setPostcodeValue("51473");
        postcode2.setRegion(region);

        postcode3 = new Postcode();
        postcode3.setPostcodeValue("52373");
        postcode3.setRegion(region);

        List<Postcode> postcodes = new ArrayList<>();
        postcodes.add(postcode1);
        postcodes.add(postcode2);
        postcodes.add(postcode3);

        when(postcodeRepository.findFirstByPostcodeValue("51373")).thenReturn(Optional.of(postcode1));
        when(postcodeRepository.findByPostcodeValueStartingWith("51373")).thenReturn(postcodes);
    }



    @Test
    void testFindFirstByPostcodeValue(){
        Optional<Postcode> postcode = postcodeRepository.findFirstByPostcodeValue("51373");
        assertTrue(postcode.isPresent());

        Postcode result = postcode.get();

        assertEquals("Nordrheinwestfalen", result.getRegion().getName());
    }

    @Test
    void testFindFirstByPostcodeValueNotFound() {
        Optional<Postcode> postcode = postcodeRepository.findFirstByPostcodeValue("99999");
        assertFalse(postcode.isPresent());
    }




    @Test
    void testFindByPostcodeValueStartingWith(){
        List<Postcode> result = postcodeRepository.findByPostcodeValueStartingWith("51373");

        assertEquals("51373", result.get(0).getPostcodeValue());
    }
    @Test
    void testFindByPostcodeValueStartingWithNotFound() {
        List<Postcode> result = postcodeRepository.findByPostcodeValueStartingWith("999");
        assertTrue(result.isEmpty());
    }
}
