package org.task.crypto.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final List<String> allowedCryptocurrencies;

    /**
     * Handles NoContentException, typically when no data is found.
     * Returns a 204 No Content response with custom error details.
     *
     * @param ex The NoContentException thrown
     * @return ResponseEntity containing the error details and HTTP status 204 No Content
     */
    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorDetails> handleNoContentException(NoContentException ex) {
        log.info("No content found: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NO_CONTENT.value(),
                ex.getMessage(),
                "No content found for the requested resource."
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NO_CONTENT);
    }

    /**
     * Handles IllegalArgumentException, typically for invalid inputs such as unsupported cryptocurrencies.
     * Returns a 400 Bad Request response with custom error details.
     *
     * @param ex The IllegalArgumentException thrown
     * @return ResponseEntity containing the error details and HTTP status 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex) {
        String allowedValues = String.join(", ", allowedCryptocurrencies);
        log.warn("Bad request: {}. Allowed values: [{}]", ex.getMessage(), allowedValues);

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid cryptocurrency symbol. Allowed values: [" + allowedValues + "]"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ResponseStatusException, typically for more specific HTTP status codes (like 404 or 500).
     * Returns a response with the status code and reason from the exception.
     *
     * @param ex The ResponseStatusException thrown
     * @return ResponseEntity containing the error details and status code
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDetails> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Response status exception: {} - {}", ex.getStatusCode(), ex.getReason(), ex);

        ErrorDetails errorDetails = new ErrorDetails(
                ex.getStatusCode().value(),
                ex.getReason(),
                "An error occurred during the request."
        );
        return new ResponseEntity<>(errorDetails, ex.getStatusCode());
    }

    /**
     * Handles generic exceptions that are not specifically caught.
     * Returns a 500 Internal Server Error response with a custom error message.
     *
     * @param ex The generic Exception thrown
     * @return ResponseEntity containing the error details and HTTP status 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "An unexpected error occurred. Please try again later."
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
