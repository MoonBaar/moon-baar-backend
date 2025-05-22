package com.moonbaar.common.oauth.dto;

import com.moonbaar.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OAuth2 인증을 통해 받아온 사용자 정보를 애플리케이션에서 사용할 형태로 변환하여 담는 DTO 클래스
 * Provider마다 다른 응답 포맷에 맞게 파싱하여 같은 형태로 통일한다.
 *
 * @param attributes            사용자 정보
 * @param userNameAttributeName 사용자 고유 ID key값
 * @param oauthId               사용자 고유 ID
 * @param oauthProvider         OAuth 로그인을 제공한 서비스
 * @param nickname              사용자 이름
 * @param profileImageUrl       사용자 프로필 이미지
 */
@Slf4j
public record OAuthAttributes(
        Map<String, Object> attributes,
        String userNameAttributeName,
        String oauthId,
        String oauthProvider,
        String nickname,
        String profileImageUrl
) {

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "naver" -> ofNaver(registrationId, "id", (Map<String, Object>) attributes.get("response"));
            default -> ofKakao(registrationId, userNameAttributeName, attributes);
        };
    }

    private static OAuthAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> response) {
        return new OAuthAttributes(
                response,
                userNameAttributeName,
                (String) response.get(userNameAttributeName),
                registrationId,
                (String) response.get("name"),
                (String) response.get("profile_image")
        );
    }

    private static OAuthAttributes ofKakao(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                String.valueOf(attributes.get(userNameAttributeName)),
                registrationId,
                (String) profile.get("nickname"),
                (String) profile.get("profile_image_url")
        );
    }

    public User toEntity() {
        return User.builder()
                .oauthId(oauthId)
                .oauthProvider(oauthProvider)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
