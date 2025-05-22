package com.moonbaar.common.oauth.handler;

import com.moonbaar.common.oauth.CustomAuthorizationRequestRepository;
import com.moonbaar.common.oauth.CustomOAuth2User;
import com.moonbaar.common.utils.JwtUtils;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.exception.UserNotFoundException;
import com.moonbaar.domain.user.repository.UserRepository;
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
    private final CustomAuthorizationRequestRepository authorizationRequestRepository;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        // 사용자 정보 가져오기
        CustomOAuth2User  oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        // JWT 발급
        String accessToken = jwtUtils.generateAccessToken(user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // DB에 refreshToken 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (accessTokenExpirationMs / 1000));
        accessCookie.setAttribute("SameSite", "None");

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
        refreshCookie.setAttribute("SameSite", "None");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        String finalRedirectUri = authorizationRequestRepository.extractRedirectUri(request);
        log.info("세션에서 가져온 redirect_uri: {}", finalRedirectUri);

        response.sendRedirect(finalRedirectUri);
    }
}
