package com.moonbaar.common.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final HttpSessionOAuth2AuthorizationRequestRepository defaultRepo =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    private static final String REDIRECT_URI_PARAM = "final_redirect_uri";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return defaultRepo.loadAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {
        // redirect_uri 쿼리 파라미터 읽기
        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null) {
            // 세션에 직접 저장
            request.getSession().setAttribute(REDIRECT_URI_PARAM, redirectUri);
        }
        defaultRepo.saveAuthorizationRequest(authorizationRequest, request, response);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return defaultRepo.removeAuthorizationRequest(request, response);
    }

    /**
     * 리다이렉트 URI를 가져오는 새로운 메서드
     */
    public String extractRedirectUri(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(REDIRECT_URI_PARAM);
    }

    /**
     * 세션에서 리다이렉트 URI를 제거하는 메서드
     */
    public void removeRedirectUri(HttpServletRequest request) {
        request.getSession().removeAttribute(REDIRECT_URI_PARAM);
    }
}
