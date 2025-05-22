package com.moonbaar.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR("G001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("G002", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("G003", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("G004", "허용되지 않는 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    HANDLE_ACCESS_DENIED("G005", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("G006", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    TOO_MANY_REQUESTS("G007", "과도한 요청을 보내셨습니다. 잠시 기다려 주세요.", HttpStatus.TOO_MANY_REQUESTS);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
