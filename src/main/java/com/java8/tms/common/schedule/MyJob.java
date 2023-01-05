package com.java8.tms.common.schedule;


import com.java8.tms.common.entity.BlackAccessToken;
import com.java8.tms.common.entity.OTP;
import com.java8.tms.common.repository.OtpRepository;
import com.java8.tms.common.repository.RefreshTokenRepository;
import com.java8.tms.common.security.jwt.BlackAccessTokenServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MyJob {
    private static final Logger logger = LoggerFactory.getLogger(MyJob.class);

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    BlackAccessTokenServiceImp blackAccessTokenService;
    
    @Autowired
    OtpRepository otpRepository;

    @Autowired
    CacheManager cacheManager;


    @Scheduled(cron = "0 1/60 * ? * * ")
    public void deleteExpiredRefreshToken() throws InterruptedException {
        Date now = new Date();
        Instant instant = now.toInstant();
        refreshTokenRepository.deleteAllByExpiryDateBefore(instant);
        logger.info("Execute deleteExpiredRefreshToken task  - " + new Date());
    }


    @Scheduled(cron = "0 1/60 * ? * * ")
    public void deleteExpiredAccessToken() throws InterruptedException {
        logger.info("Execute deleteExpiredAccessToken task  - " + new Date());
        Date now = new Date();
        Instant instant = now.toInstant();
        List<BlackAccessToken> blackAccessTokenList = blackAccessTokenService.findAllByExpiryDateBefore(instant);
        if (!blackAccessTokenList.isEmpty()) {
            List<String> accessTokens = blackAccessTokenList.stream()
                    .map(BlackAccessToken::getAccessToken)
                    .collect(Collectors.toList());
            this.deleteExpiredAccessTokenCache(accessTokens);
            blackAccessTokenService.deleteAllByAccessTokenIn(accessTokens);
        }
    }

    private void deleteExpiredAccessTokenCache(List<String> accessTokens){
        List<String> tmp = new ArrayList<>();
        accessTokens.forEach(s -> {
            boolean rs = cacheManager.getCache("blackAccessToken").evictIfPresent(s);
            if (rs) {
                tmp.add(s);
            }
        });
        if (!tmp.isEmpty()) {
            logger.info("Cleaned up token [" + tmp + "] from cache");
        }
    }

    @Scheduled(cron = "0 0 0/6 ? * * ")
    public void clearSystemCache() throws InterruptedException{
        cacheManager.getCacheNames().parallelStream().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
        logger.info("Execute clearSystemCache task  - " + new Date());
    }
    
    @Scheduled(cron = "0 */30 * ? * *")
    public void cleanExpiredOtp() throws InterruptedException{
        List<OTP> listOtp = otpRepository.findByOtpExpiredTimeBefore(currentDateTimeWithSubMinutes(4));
        otpRepository.deleteAll(listOtp);
        logger.info("Execute cleanExpiredOtp task  - " + new Date());
    }
    
    @SuppressWarnings("deprecation")
	public Date currentDateTimeWithSubMinutes(int n) {
        Date now = new Date();
        int minute = now.getMinutes() - n;
        Date nowDateAfterSubMinutes = new Date();
        nowDateAfterSubMinutes.setMinutes(minute);
        return nowDateAfterSubMinutes;
    }
}
