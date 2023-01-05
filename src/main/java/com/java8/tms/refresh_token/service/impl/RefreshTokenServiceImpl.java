package com.java8.tms.refresh_token.service.impl;

import com.java8.tms.common.repository.RefreshTokenRepository;
import com.java8.tms.refresh_token.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
}
