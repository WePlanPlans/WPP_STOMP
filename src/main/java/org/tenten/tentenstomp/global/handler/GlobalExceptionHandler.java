package org.tenten.tentenstomp.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.tenten.tentenstomp.global.exception.GlobalException;
import org.tenten.tentenstomp.global.response.ErrorResponse;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected final ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex,
                                                                        WebRequest request
    ) {

        log.error(ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorMessage(ex.getMessage())
            .status(ex.getErrorCode().value())
            .build();

        return new ResponseEntity<>(errorResponse, ex.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request
    ) {

        log.error(ex.getMessage());
        Arrays.stream(ex.getStackTrace()).forEach(
            stackTraceElement -> log.error(stackTraceElement.toString())
        );

        return new ResponseEntity<>(ErrorResponse.internalServerError(ex.getMessage()),
            INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected final ResponseEntity<ErrorResponse> methodValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("handling {}, message : {}", ex.getClass().toString(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorMessage(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}