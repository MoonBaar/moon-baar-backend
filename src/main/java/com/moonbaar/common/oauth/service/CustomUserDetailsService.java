package com.moonbaar.common.oauth.service;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.service.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserProvider userProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userProvider.getUserById(Long.valueOf(username));
        return new CustomUserDetails(user);
    }
}
