package com.moonbaar.domain.like.controller;

import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class LikeController {

    // 임시 유저 아이디
    private Long userId = 1L;

    private final LikeService likeService;

    @PostMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> likeEvent(@PathVariable Long eventId) {
        LikeResponse response = likeService.likeEvent(eventId, userId);
        return ResponseEntity.ok(response);
    }

}
