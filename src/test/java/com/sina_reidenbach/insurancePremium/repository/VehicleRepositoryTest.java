package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class VehicleRepositoryTest {
    private AutoCloseable closeable;
    @Mock
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Vehicle vehicle = new Vehicle("SUV",1.5);
        when(vehicleRepository.findByName("SUV")).thenReturn(vehicle);
        when(vehicleRepository.findById(14L)).thenReturn(Optional.of(vehicle));
    }
    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testFindByVehicleName() {
        Vehicle result = vehicleRepository.findByName("SUV");
        assertNotNull(result);
        assertEquals("SUV", result.getName());
        assertEquals(1.5, result.getFactor());
    }

    @Test
    void testFindByVehicleNameNotFound() {
        Vehicle result = vehicleRepository.findByName("Limousine");
        assertNull(result);
    }

    @Test
    void testFindById(){
        Optional<Vehicle> vehicle = vehicleRepository.findById(14L);

        assertTrue(vehicle.isPresent());
        Vehicle result = vehicle.get();

        assertNotNull(result);
        assertEquals("SUV", result.getName());
        assertEquals(1.5, result.getFactor());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Vehicle> vehicle = vehicleRepository.findById(999L);
        assertFalse(vehicle.isPresent());
    }

}