package com.java8.tms.common.security.jwt;

import com.java8.tms.common.entity.BlackAccessToken;

import java.time.Instant;
import java.util.List;

public interface BlackAccessTokenService {
    BlackAccessToken findByAccessToken(String accessToken);

    List<BlackAccessToken> findAllByExpiryDateBefore(Instant instant);

    BlackAccessToken save(BlackAccessToken blackAccessToken);

    void deleteAllByAccessTokenIn(List<String> accessTokens);
}
