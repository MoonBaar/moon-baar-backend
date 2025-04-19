package com.moonbaar.common.config;

import com.moonbaar.common.oauth.TokenBlackList;
import com.moonbaar.common.utils.JwtUtils;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
}
