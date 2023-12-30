package org.tenten.tentenstomp.global.component.exception;

import org.springframework.http.HttpStatus;
import org.tenten.tentenstomp.global.exception.GlobalException;

public class OpenApiException extends GlobalException {
    public OpenApiException(String message, HttpStatus errorCode) {
        super(message, errorCode);
    }
}
