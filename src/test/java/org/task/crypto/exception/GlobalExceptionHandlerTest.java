package org.task.crypto.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    @Mock
    private List<String> allowedCryptocurrencies;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler(allowedCryptocurrencies);
    }

    @Test
    void testHandleNoContentException() {
        NoContentException exception = new NoContentException("No data available");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleNoContentException(exception);

        assertEquals(204, response.getStatusCodeValue());
        assertEquals("No content found for the requested resource.", response.getBody().detailedMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        when(allowedCryptocurrencies.iterator()).thenReturn(List.of("BTC", "ETH", "XRP").iterator());

        IllegalArgumentException exception = new IllegalArgumentException("Invalid cryptocurrency");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid cryptocurrency symbol. Allowed values: [BTC, ETH, XRP]", response.getBody().detailedMessage());
    }

    @Test
    void testHandleResponseStatusException() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleResponseStatusException(exception);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not found", response.getBody().message());
        assertEquals("An error occurred during the request.", response.getBody().detailedMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception exception = new Exception("General exception");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleGenericException(exception);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("General exception", response.getBody().message());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().detailedMessage());
    }

    @Test
    void testHandleValidationExceptions() {
        FieldError fieldError = new FieldError("object", "field", "must not be empty");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, new BindException("object", "field"));
        exception.getBindingResult().addError(fieldError);

        Map<String, String> errors = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("must not be empty", errors.get("field"));
    }
}
