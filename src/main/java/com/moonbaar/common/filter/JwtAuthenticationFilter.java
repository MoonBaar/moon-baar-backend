package com.moonbaar.common.filter;

import com.moonbaar.common.oauth.TokenBlackList;
import com.moonbaar.common.oauth.service.CustomUserDetailsService;
import com.moonbaar.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
        if (accessToken == null || !jwtUtils.isValid(accessToken) || tokenBlackList.contains(accessToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 인증 객체 생성 및 SecurityContext 등록
        Long userId = jwtUtils.getUserId(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
