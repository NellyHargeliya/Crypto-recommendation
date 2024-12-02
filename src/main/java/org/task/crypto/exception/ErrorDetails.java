package org.task.crypto.exception;

public record ErrorDetails(
        Integer code,
        String message,
        String detailedMessage
) {
}
