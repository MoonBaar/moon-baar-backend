package com.moonbaar.domain.user.controller;

import com.moonbaar.domain.like.dto.LikedEventListResponse;
import com.moonbaar.domain.like.exception.InvalidLikeParamsException;
import com.moonbaar.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final LikeService likeService;

    @GetMapping("/me/likes")
    public ResponseEntity<LikedEventListResponse> getLikedEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String order) {

        LikedEventListResponse response = new LikedEventListResponse();
        return ResponseEntity.ok(response);
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new InvalidLikeParamsException();
        }
    }
}
