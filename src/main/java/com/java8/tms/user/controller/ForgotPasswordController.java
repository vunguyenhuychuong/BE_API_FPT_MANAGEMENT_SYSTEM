package com.java8.tms.user.controller;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.user.dto.ChangePasswordDTO;
import com.java8.tms.user.dto.ForgotPasswordDTO;
import com.java8.tms.user.dto.OtpVerificationDTO;

import com.java8.tms.user.service.impl.ForgotPasswordServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("api/v1")
@CrossOrigin
public class ForgotPasswordController {

	@Autowired
	private ForgotPasswordServiceImpl forgotPasswordServiceImpl;


	/**
	 * 
	 * <p>
	 * Check email in Database and send email with an Otp code
	 * </p>
	 *
	 * @param forgotPasswordDTO
	 * @return ResponseObject
	 *
	 * @author Tuan Khanh, The Quang
	 */
	@PostMapping("forgot-password")
	@SecurityRequirements
	public ResponseEntity<ResponseObject> sendOTP(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
		return forgotPasswordServiceImpl.sendOTP(forgotPasswordDTO);
	}

	/**
	 * 
	 * <p>
	 * Check the Otp code from user
	 * </p>
	 *
	 * @param otpVerificationDTO
	 * @return ResponseObject
	 *
	 * @author Tuan Khanh, The Quang
	 */
	@PostMapping("verification")
	@SecurityRequirements
	public ResponseEntity<ResponseObject> verifyOtp(@Valid @RequestBody OtpVerificationDTO otpVerificationDTO) {
		return forgotPasswordServiceImpl.verifyOTP(otpVerificationDTO);
	}

	/**
	 * 
	 * <p>
	 * Check if both password are correct and change user's password
	 * </p>
	 *
	 * @param changePasswordDTO
	 * @return ResponseObject
	 *
	 * @author Tuan Khanh, The Quang
	 */
	@PutMapping("change-password")
	@SecurityRequirements
	public ResponseEntity<ResponseObject> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
		return forgotPasswordServiceImpl.changePassword(changePasswordDTO);
	}
}
