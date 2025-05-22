package com.moonbaar.common.test.controller;

import com.moonbaar.common.utils.JwtUtils;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestAuthController {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    private static final String TEST_OAUTH_ID = "test-oauth-id";
    private static final String TEST_OAUTH_PROVIDER = "test";
    private static final String DEFAULT_REDIRECT_PATH = "/login-success";

    @GetMapping("/login")
    public void testLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 테스트용 사용자 찾기 또는 생성
        User testUser = findOrCreateTestUser();

        // JWT 토큰 발급
        String accessToken = jwtUtils.generateAccessToken(testUser.getId());
        String refreshToken = jwtUtils.generateRefreshToken(testUser.getId());

        // DB에 refreshToken 저장
        testUser.updateRefreshToken(refreshToken);
        userRepository.save(testUser);

        // 쿠키 설정
        addTokenToCookie(response, "accessToken", accessToken,
                (int)(jwtUtils.getAccessTokenExpiration() / 1000));
        addTokenToCookie(response, "refreshToken", refreshToken,
                (int)(jwtUtils.getRefreshTokenExpiration() / 1000));

        // Referer 헤더에서 도메인 추출 후 리다이렉트
        String redirectUrl = determineRedirectUrl(request);
        log.info("테스트 로그인 성공: user_id={}, redirect_url={}", testUser.getId(), redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    private User findOrCreateTestUser() {
        Optional<User> existingUser = userRepository.findByOauthIdAndOauthProvider(
                TEST_OAUTH_ID, TEST_OAUTH_PROVIDER);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = User.builder()
                .oauthId(TEST_OAUTH_ID)
                .oauthProvider(TEST_OAUTH_PROVIDER)
                .nickname("테스트유저")
                .profileImageUrl("https://velog.velcdn.com/images/zhy2on/post/fed3dd30-3ef5-485a-8ad2-d1aa05c00fa9/image.png")
                .build();

        return userRepository.save(newUser);
    }

    private void addTokenToCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    private String determineRedirectUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            return DEFAULT_REDIRECT_PATH;
        }

        try {
            String domain = extractDomain(referer);
            log.info("Referer 헤더에서 추출한 domain: {}", domain);
            return domain + DEFAULT_REDIRECT_PATH;
        } catch (URISyntaxException e) {
            log.error("Referer 헤더에서 도메인 추출 실패: {}", referer, e);
            return DEFAULT_REDIRECT_PATH;
        }
    }

    private String extractDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getScheme() + "://" + uri.getHost();
        if (uri.getPort() != -1) {
            domain += ":" + uri.getPort();
        }
        return domain;
    }
}
