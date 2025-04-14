package com.moonbaar.common.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonbaar.common.oauth.CustomOAuth2User;
import com.moonbaar.common.utils.JwtUtils;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${frontend.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        // 사용자 정보 가져오기
        CustomOAuth2User  oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(()-> new IllegalArgumentException("로그인된 사용자가 DB에 존재하지 않습니다."));

        // JWT 발급
        String accessToken = jwtUtils.generateAccessToken(user.getId(), "ROLE_USER");

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30); // 30분
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);

        response.sendRedirect(redirectUrl + "/login-success");
    }
}
