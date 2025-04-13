package com.moonbaar.common.service;

import com.moonbaar.common.dto.OAuthAttributes;
import com.moonbaar.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // OAuth2 Provider 이름 ex) kakao, naver
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 사용자의 고유 ID의 Key 값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // attributes 표준 매핑
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 회원가입 또는 프로필 업데이트 처리
        userService.saveOrUpdate(attributes, registrationId);

        // Spring Security가 인식할 사용자 객체 반환
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),
                attributes.attributes(),
                attributes.userNameAttributeName()
        );
    }

}
