package com.moonbaar.common.oauth;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TokenBlackList {

    // key: accessToken, value: 만료시간
    private final Map<String, LocalDateTime> blacklist = new ConcurrentHashMap<>();

    public void add(String token, LocalDateTime expiresAt) {
        blacklist.put(token, expiresAt);
    }

    public boolean contains(String token) {
        return blacklist.containsKey(token);
    }

    public void removeExpiredTokens() {
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(LocalDateTime.now()));
    }

}
