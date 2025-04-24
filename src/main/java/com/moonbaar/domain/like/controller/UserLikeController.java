package com.moonbaar.domain.like.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.like.dto.LikedEventListRequest;
import com.moonbaar.domain.like.dto.LikedEventListResponse;
import com.moonbaar.domain.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final LikeService likeService;

    @GetMapping("/me/likes")
    public ResponseEntity<LikedEventListResponse> getLikedEvents(
            @ModelAttribute @Valid LikedEventListRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        LikedEventListResponse response = likeService.getLikedEvents(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }
}
