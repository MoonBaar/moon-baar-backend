package com.moonbaar.domain.visit.exception;

import com.moonbaar.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VisitErrorCode implements ErrorCode {

    RECENTLY_VISITED("V001", "재방문 가능 시간이 아닙니다.", HttpStatus.CONFLICT),
    INVALID_LOCATION("V002", "유효하지 않은 위치입니다. 행사장과 너무 멉니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
