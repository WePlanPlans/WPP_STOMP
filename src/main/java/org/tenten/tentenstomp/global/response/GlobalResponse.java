package org.tenten.tentenstomp.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalResponse {
    private int status;
    private String message;

    public static GlobalResponse ok(String message) {
        return new GlobalResponse(OK.value(), message);
    }

    public static GlobalResponse created(String message) {
        return new GlobalResponse(CREATED.value(), message);
    }
}