package com.java8.tms.user.service;

import org.springframework.http.ResponseEntity;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.user.dto.SignupUserDTO;

public interface SignupService {
	ResponseEntity<ResponseObject> createNewAccount(SignupUserDTO signupUserDTO);
}
