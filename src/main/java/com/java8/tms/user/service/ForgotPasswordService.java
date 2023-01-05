package com.java8.tms.user.service;

import org.springframework.http.ResponseEntity;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.user.dto.ChangePasswordDTO;
import com.java8.tms.user.dto.ForgotPasswordDTO;
import com.java8.tms.user.dto.OtpVerificationDTO;

public interface ForgotPasswordService {
	ResponseEntity<ResponseObject> sendOTP(ForgotPasswordDTO forgotPasswordDTO);
	ResponseEntity<ResponseObject> verifyOTP(OtpVerificationDTO otpVerificationDTO);
	ResponseEntity<ResponseObject> changePassword(ChangePasswordDTO changePasswordDTO);
}
