package com.moonbaar.common.oauth.service;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.exception.UserNotFoundException;
import com.moonbaar.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(username)).orElseThrow(UserNotFoundException::new);
        return new CustomUserDetails(user);
    }
}
