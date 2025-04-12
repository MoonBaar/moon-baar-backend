package com.moonbaar.domain.like.exception;

import com.moonbaar.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements ErrorCode {

    ALREADY_LIKED_EVENT("L001", "이미 좋아요한 행사입니다.", HttpStatus.CONFLICT),
    LIKE_NOT_FOUND("L002", "좋아요 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
