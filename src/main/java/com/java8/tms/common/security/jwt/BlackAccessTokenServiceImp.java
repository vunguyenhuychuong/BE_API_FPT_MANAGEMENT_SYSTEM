package com.java8.tms.common.security.jwt;

import com.java8.tms.common.entity.BlackAccessToken;
import com.java8.tms.common.repository.BlackAccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BlackAccessTokenServiceImp implements BlackAccessTokenService{

    @Autowired
    BlackAccessTokenRepository blackAccessTokenRepository;


    @Override
    @Cacheable(value = "blackAccessToken", unless="#result == null")
    public BlackAccessToken findByAccessToken(String accessToken) {
        return blackAccessTokenRepository.findByAccessToken(accessToken);
    }

    @Override
    public List<BlackAccessToken> findAllByExpiryDateBefore(Instant instant) {
        return blackAccessTokenRepository.findAllByExpiryDateBefore(instant);
    }

    @Override
    @CachePut(value = "blackAccessToken", key = "#blackAccessToken.accessToken", unless="#result == null")
    public BlackAccessToken save(BlackAccessToken blackAccessToken) {
        return blackAccessTokenRepository.save(blackAccessToken);
    }


    @Override
    public void deleteAllByAccessTokenIn(List<String> accessTokens) {
        blackAccessTokenRepository.deleteAllByAccessTokenIn(accessTokens);
    }
}
