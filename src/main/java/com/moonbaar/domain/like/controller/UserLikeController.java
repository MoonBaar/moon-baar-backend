package com.moonbaar.domain.like.controller;

import com.moonbaar.domain.like.dto.LikedEventListRequest;
import com.moonbaar.domain.like.dto.LikedEventListResponse;
import com.moonbaar.domain.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserLikeController {

    // 임시 유저 아이디
    private Long MOCK_USER_ID = 1L;

    private final LikeService likeService;

    @GetMapping("/me/likes")
    public ResponseEntity<LikedEventListResponse> getLikedEvents(
            @ModelAttribute @Valid LikedEventListRequest request) {

        LikedEventListResponse response = likeService.getLikedEvents(MOCK_USER_ID, request);
        return ResponseEntity.ok(response);
    }
}
