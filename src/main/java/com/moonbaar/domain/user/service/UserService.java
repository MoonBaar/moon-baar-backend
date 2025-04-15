package com.moonbaar.domain.user.service;


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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public User saveOrUpdate(OAuthAttributes attributes, String registrationId) {
        return userRepository.findByOauthIdAndOauthProvider(attributes.oauthId(), registrationId)
                .map(entity -> entity.update(attributes.nickname(), attributes.profileImageUrl()))
                .orElseGet(() -> userRepository.save(attributes.toEntity()));
    }

    // Refresh Token로 Access Token 재발급
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refreshToken 추출
        String refreshToken = getCookieValueByName(request, "refreshToken");

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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UserAccessDeniedException();
        }

        // Access Token 새로 발급
        String accessToken = jwtUtils.generateAccessToken(userId);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30); // 30분
        accessCookie.setAttribute("SameSite", "Lax");

        response.addCookie(accessCookie);
    }


    /**
     * 요청에서 특정 이름의 쿠키 값을 반환
     * @param request   HttpServletRequest 객체
     * @param name      찾고자 하는 Cookie 이름
     * @return          찾고자 하는 Cookie 값, 없으면 null
     */
    private String getCookieValueByName(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
