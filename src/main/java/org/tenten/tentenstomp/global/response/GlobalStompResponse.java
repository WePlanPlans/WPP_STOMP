package org.tenten.tentenstomp.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.tenten.tentenstomp.global.common.constant.ResponseConstant;

import static org.tenten.tentenstomp.global.common.constant.ResponseConstant.SUCCESS;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalStompResponse <T> {
    private int status;
    private String message;
    private T data;
    public static <T> GlobalStompResponse<T> ok(T data) {
        return new GlobalStompResponse<>(HttpStatus.OK.value(), SUCCESS, data);
    }
}
