package org.task.crypto.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDetailsTest {

    @Test
    void testConstructor() {
        Integer code = 404;
        String message = "Not Found";
        String detailedMessage = "The requested resource could not be found on the server.";

        ErrorDetails errorDetails = new ErrorDetails(code, message, detailedMessage);

        assertEquals(code, errorDetails.code());
        assertEquals(message, errorDetails.message());
        assertEquals(detailedMessage, errorDetails.detailedMessage());
    }

    @Test
    void testToString() {
        Integer code = 500;
        String message = "Internal Server Error";
        String detailedMessage = "An unexpected error occurred on the server.";

        ErrorDetails errorDetails = new ErrorDetails(code, message, detailedMessage);
        String expectedString = "ErrorDetails[code=" + code + ", message=" + message + ", detailedMessage=" + detailedMessage + "]";

        assertEquals(expectedString, errorDetails.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Integer code = 400;
        String message = "Bad Request";
        String detailedMessage = "The request could not be understood by the server.";

        ErrorDetails errorDetails1 = new ErrorDetails(code, message, detailedMessage);
        ErrorDetails errorDetails2 = new ErrorDetails(code, message, detailedMessage);

        assertEquals(errorDetails1, errorDetails2);
        assertEquals(errorDetails1.hashCode(), errorDetails2.hashCode());
    }

    @Test
    void testNotEqual() {
        Integer code1 = 400;
        String message1 = "Bad Request";
        String detailedMessage1 = "The request could not be understood by the server.";

        Integer code2 = 404;
        String message2 = "Not Found";
        String detailedMessage2 = "The requested resource could not be found.";

        ErrorDetails errorDetails1 = new ErrorDetails(code1, message1, detailedMessage1);
        ErrorDetails errorDetails2 = new ErrorDetails(code2, message2, detailedMessage2);

        assertNotEquals(errorDetails1, errorDetails2);
    }
}
