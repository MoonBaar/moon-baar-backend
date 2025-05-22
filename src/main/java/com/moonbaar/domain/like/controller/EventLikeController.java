package com.moonbaar.domain.like.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventLikeController {

    private final LikeService likeService;

    @PostMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> likeEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        LikeResponse response = likeService.likeEvent(userDetails.getUser(), eventId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> unlikeEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        LikeResponse response = likeService.unlikeEvent(userDetails.getUser(), eventId);
        return ResponseEntity.ok(response);
    }
}
