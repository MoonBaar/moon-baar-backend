package com.moonbaar.domain.user.service;


import com.moonbaar.common.dto.OAuthAttributes;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdate(OAuthAttributes attributes, String registrationId) {
        return userRepository.findByOauthIdAndOauthProvider(attributes.oauthId(), registrationId)
                .map(entity -> entity.update(attributes.nickname(), attributes.profileImageUrl()))
                .orElseGet(() -> userRepository.save(attributes.toEntity()));
    }

}
