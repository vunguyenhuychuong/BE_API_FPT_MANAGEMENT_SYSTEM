package com.java8.tms.common.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.OTP;

@Repository
public interface OtpRepository  extends JpaRepository<OTP, UUID> {
	OTP findOtpByUserId(UUID userId);
	
	Optional<OTP> findOtpById(UUID otpId);
	
	@Query("SELECT o FROM OTP o WHERE o.otpExpiredTime < ?1 ")
	List<OTP> findByOtpExpiredTimeBefore(Date now);
}
