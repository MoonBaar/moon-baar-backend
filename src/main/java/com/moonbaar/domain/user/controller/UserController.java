package com.moonbaar.domain.user.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.user.dto.UserInfoResponse;
import com.moonbaar.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        userService.refreshAccessToken(request, response);
        return ResponseEntity.ok("Access Token 재발급 완료");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getLoginUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(UserInfoResponse.from(userDetails.getUser()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteLoginUser(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(request, response, userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
