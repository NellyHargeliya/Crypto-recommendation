package org.task.crypto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoContentException extends RuntimeException {

    public NoContentException(String message) {
        super(message);
    }
}
