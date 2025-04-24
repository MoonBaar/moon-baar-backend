package com.moonbaar.common.config;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.common.oauth.TokenBlackList;
import com.moonbaar.common.oauth.service.CustomUserDetailsService;
import com.moonbaar.common.utils.JwtUtils;
import com.moonbaar.domain.user.entity.User;
import java.time.LocalDateTime;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll());

        return http.build();
    }

    @Bean
    @Primary
    public JwtUtils jwtUtils() {
        return Mockito.mock(JwtUtils.class);
    }

    @Bean
    @Primary
    public TokenBlackList tokenBlackList() {
        return Mockito.mock(TokenBlackList.class);
    }

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    public static CustomUserDetails createTestUserDetails(Long userId, String nickname) {
        User testUser = User.builder()
                .oauthId("test-oauth-id")
                .oauthProvider("test")
                .nickname(nickname)
                .profileImageUrl("https://example.com/profile.jpg")
                .build();

        ReflectionTestUtils.setField(testUser, "id", userId);
        ReflectionTestUtils.setField(testUser, "createdAt", LocalDateTime.now());

        return new CustomUserDetails(testUser);
    }
}
