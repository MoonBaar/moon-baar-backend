package com.moonbaar.domain.user.service;


import com.moonbaar.common.oauth.TokenBlackList;
import com.moonbaar.common.oauth.dto.OAuthAttributes;
import com.moonbaar.common.utils.JwtUtils;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.exception.UserAccessDeniedException;
import com.moonbaar.domain.user.exception.UserNotFoundException;
import com.moonbaar.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final TokenBlackList tokenBlackList;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Transactional
    public User saveOrUpdate(OAuthAttributes attributes, String registrationId) {
        return userRepository.findByOauthIdAndOauthProvider(attributes.oauthId(), registrationId)
                .map(entity -> entity.update(attributes.nickname(), attributes.profileImageUrl()))
                .orElseGet(() -> userRepository.save(attributes.toEntity()));
    }

    // Refresh Token로 Access Token 재발급
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refreshToken 추출
        String refreshToken = jwtUtils.resolveToken(request, "refreshToken");

        // refreshToken 누락
        if (refreshToken == null) {
            throw new UserAccessDeniedException();
        }

        // 유효하지 않은 토큰
        if (!jwtUtils.isValid(refreshToken)) {
            throw new UserAccessDeniedException();
        }

        // DB에 저장된 refreshToken과 비교
        Long userId = jwtUtils.getUserId(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UserAccessDeniedException();
        }

        // Access Token 새로 발급
        String accessToken = jwtUtils.generateAccessToken(userId);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (accessTokenExpirationMs / 1000));
        accessCookie.setAttribute("SameSite", "Lax");

        response.addCookie(accessCookie);
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 블랙리스트에 accessToken  추가
        String accessToken = jwtUtils.resolveToken(request, "accessToken");
        if (accessToken != null && jwtUtils.isValid(accessToken)) {
            LocalDateTime expiresAt = jwtUtils.getExpiration(accessToken);
            tokenBlackList.add(accessToken, expiresAt);
        }

        // DB에서 refreshToken 삭제
        String refreshToken = jwtUtils.resolveToken(request, "refreshToken");
        if (refreshToken != null && jwtUtils.isValid(refreshToken)) {
            Long userId = jwtUtils.getUserId(refreshToken);
            userRepository.findById(userId).ifPresent(User::removeRefreshToken);
        }

        // 쿠키에서 토큰 제거
        deleteCookie("accessToken", response);
        deleteCookie("refreshToken", response);
    }

    private void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
