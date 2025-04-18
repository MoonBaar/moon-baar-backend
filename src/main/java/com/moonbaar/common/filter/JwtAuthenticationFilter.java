package com.moonbaar.common.filter;

import com.moonbaar.common.oauth.TokenBlackList;
import com.moonbaar.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlackList tokenBlackList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtils.resolveToken(request, "accessToken");
        if (accessToken == null || !jwtUtils.isValid(accessToken) || tokenBlackList.contains(accessToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/login") ||
                path.startsWith("/api/oauth2") ||
                path.startsWith("/api/events") ||
                path.startsWith("/api/categories") ||
                path.startsWith("/api/districts") ||
                path.startsWith("/api/users");
    }
}
