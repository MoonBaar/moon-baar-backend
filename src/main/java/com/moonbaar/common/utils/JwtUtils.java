package com.moonbaar.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;
    private Key key;

    @Getter
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 생성
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    // 요청에서 토큰 추출
    public String resolveToken(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // token에서 userId 추출
    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // 토큰 유효시간 추출
    public LocalDateTime getExpiration(String token) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        Instant instant = expiration.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    // 토큰 유효성 검사
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | SignatureException e) {
            log.error("서명 검증 실패 : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("손상된 토큰 : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰 : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("빈 토큰 혹은 잘못된 값 : {}", e.getMessage());
        } catch (JwtException e) {
            log.error("기타 JWT 오류 : {}", e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 예외 : {}", e.getMessage());
        }
        return false;
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
