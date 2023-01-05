package com.java8.tms.common.repository;

import com.java8.tms.common.entity.BlackAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BlackAccessTokenRepository extends JpaRepository<BlackAccessToken, UUID> {

    BlackAccessToken findByAccessToken(String accessToken);

    @Query("select b from BlackAccessToken b where b.expiryDate < ?1")
    List<BlackAccessToken> findAllByExpiryDateBefore(Instant instant);

    @Transactional
    @Modifying
    @Query("delete from BlackAccessToken b where b.accessToken in ?1")
    void deleteAllByAccessTokenIn(List<String> accessTokens);

}
