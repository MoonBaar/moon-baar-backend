package com.moonbaar.common.filter;

import com.moonbaar.common.oauth.TokenBlackList;
import com.moonbaar.common.oauth.service.CustomUserDetailsService;
import com.moonbaar.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlackList tokenBlackList;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtils.resolveToken(request, "accessToken");

        if (accessToken != null && jwtUtils.isValid(accessToken) && !tokenBlackList.contains(accessToken)) {
            // ✅ 정상 토큰이면 실제 사용자 인증 처리
            Long userId = jwtUtils.getUserId(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // ✅ 토큰이 없거나 유효하지 않더라도, 임시 사용자로 인증 처리 (개발용)
            UserDetails devUser = userDetailsService.loadUserByUsername(String.valueOf("1"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(devUser, null, devUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/login") ||
                path.startsWith("/api/oauth2") ||
                path.equals("/api/events") ||
                path.matches("/api/events/\\d+$") ||
                path.startsWith("/api/categories") ||
                path.startsWith("/api/districts") ||
                path.startsWith("/api/users/refresh");
    }
}
