package com.java8.tms.common.security.jwt;


import com.java8.tms.common.entity.RefreshToken;
import com.java8.tms.common.repository.RefreshTokenRepository;
import com.java8.tms.common.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenProvider {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    CacheManager cacheManager;
    @Value("${app.refreshTokenDurationMs}")
    long refreshTokenDurationMs;

    @Cacheable("refreshToken")
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByEmail(email).get());

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        RefreshToken refreshTokenEncrypt = new RefreshToken(refreshToken);
        refreshTokenEncrypt.setToken(DigestUtils.sha3_256Hex(refreshToken.getToken()));

        cacheManager.getCache("refreshToken").put(DigestUtils.sha3_256Hex(refreshToken.getToken()), refreshToken );

        refreshTokenRepository.save(refreshTokenEncrypt);
        return refreshToken;
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token was expired");
        }
        return token;
    }

    @Transactional
    public RefreshToken deleteByUserId(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("hi"));
        refreshTokenRepository.deleteAllByUser_Id(userId);
        return refreshToken;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
