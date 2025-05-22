package com.moonbaar.common.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String REDIRECT_URI_PARAM = "final_redirect_uri";
    private static final String DEFAULT_REDIRECT_PATH = "/login-success";

    private final HttpSessionOAuth2AuthorizationRequestRepository defaultRepo;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return defaultRepo.loadAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String finalRedirectUri = determineRedirectUri(request);
        storeRedirectUri(request, finalRedirectUri);
        defaultRepo.saveAuthorizationRequest(authorizationRequest, request, response);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return defaultRepo.removeAuthorizationRequest(request, response);
    }

    public String extractRedirectUri(HttpServletRequest request) {
        String finalRedirectUri = (String) request.getSession().getAttribute(REDIRECT_URI_PARAM);
        request.getSession().removeAttribute(REDIRECT_URI_PARAM);
        return finalRedirectUri;
    }

    private String determineRedirectUri(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            return "/";
        }

        try {
            String domain = extractDomain(referer);
            log.info("Referer 헤더에서 추출한 domain: {}", domain);
            return domain + DEFAULT_REDIRECT_PATH;
        } catch (URISyntaxException e) {
            log.error("Referer 헤더에서 도메인 추출 실패: {}", referer, e);
            return "/";
        }
    }

    private void storeRedirectUri(HttpServletRequest request, String redirectUri) {
        request.getSession().setAttribute(REDIRECT_URI_PARAM, redirectUri);
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
