package com.moonbaar.domain.visit.exception;

import com.moonbaar.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VisitErroCode implements ErrorCode {

    ALREADY_VISITED("V001", "최근에 이미 방문한 행사입니다.", HttpStatus.CONFLICT),
    INVALID_LOCATION("V002", "유효하지 않은 위치입니다. 행사장과 너무 멉니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
