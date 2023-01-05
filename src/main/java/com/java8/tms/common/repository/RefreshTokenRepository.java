package com.java8.tms.common.repository;


import com.java8.tms.common.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Query("select r from RefreshToken r where r.user.id = ?1")
    Optional<RefreshToken> findByUser_Id(UUID userId);

    @Query("delete from RefreshToken r where r.user.id = ?1")
    @Modifying
    @Transactional
    void deleteAllByUser_Id(UUID id);

    @Transactional
    @Modifying
    @Query("delete from RefreshToken r where r.token = ?1")
    void deleteByToken(String token);

    @Transactional
    @Modifying
    @Query("delete from RefreshToken r where r.expiryDate <= ?1")
    void deleteAllByExpiryDateBefore(Instant now);
}
