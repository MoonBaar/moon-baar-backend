package com.moonbaar.common.security;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.oauth.CustomUserDetails;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CustomUserDetails principal = SecurityTestConfig.createTestUserDetails(
                annotation.id(), annotation.nickname());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, Collections.singletonList(() -> "ROLE_USER"));
        context.setAuthentication(auth);
        return context;
    }
}
