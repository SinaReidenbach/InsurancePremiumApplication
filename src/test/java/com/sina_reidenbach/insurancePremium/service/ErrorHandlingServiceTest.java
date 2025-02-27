package com.sina_reidenbach.insurancePremium.service;

import com.sina_reidenbach.insurancePremium.InsurancePremiumApplication;
import com.sina_reidenbach.insurancePremium.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = InsurancePremiumApplication.class)
class ErrorHandlingServiceTest {

    @Mock
    private Model model;

    @InjectMocks
    private ErrorHandlingService errorHandlingService;

    private final Logger logger = LoggerFactory.getLogger(ErrorHandlingService.class);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleError() {
        String message = "Test error message";
        String result = errorHandlingService.handleError(model, message);

        verify(model).addAttribute("error", message);
        assertEquals("index", result);
    }


    @Test
    void testLogAndThrowError() {
        String message = "Test exception message";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            errorHandlingService.logAndThrowError(message);
        });
        assertEquals(message, exception.getMessage());
    }


    @Test
    void testHandleValidationErrors() {
        List<String> errorMessages = List.of("Error 1", "Error 2");

        ResponseEntity<ErrorResponse> response = errorHandlingService.handleValidationErrors(errorMessages);

        assertEquals(400, response.getStatusCodeValue());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Fehlerhafte Eingabewerte", body.getError());
        assertEquals("Error 1, Error 2", body.getMessage());
    }

    @Test
    void testHandleServerError() {
        Exception exception = new RuntimeException("Server failure");

        ResponseEntity<ErrorResponse> response = errorHandlingService.handleServerError(exception);

        assertEquals(500, response.getStatusCodeValue());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Serverfehler", body.getError());
        assertEquals("Ein unerwarteter Fehler ist aufgetreten", body.getMessage());
    }
}