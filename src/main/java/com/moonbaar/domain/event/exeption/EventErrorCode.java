package com.moonbaar.domain.event.exeption;

import com.moonbaar.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventErrorCode implements ErrorCode {

    EVENT_NOT_FOUND("E001", "요청한 행사를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_EVENT_PARAMS("E002", "잘못된 행사 검색 파라미터입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
