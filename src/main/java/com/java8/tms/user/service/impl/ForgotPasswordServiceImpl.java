package com.java8.tms.user.service.impl;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.OTP;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.repository.OtpRepository;
import com.java8.tms.user.custom_exception.OtpNotFoundException;
import com.java8.tms.user.dto.ChangePasswordDTO;
import com.java8.tms.user.dto.EmailDetailsDTO;
import com.java8.tms.user.dto.ForgotPasswordDTO;
import com.java8.tms.user.dto.OtpVerificationDTO;
import com.java8.tms.user.service.ForgotPasswordService;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private OtpRepository otpRepository;
	@Autowired
	private EmailServiceImpl emailServiceImpl;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public ResponseEntity<ResponseObject> sendOTP(ForgotPasswordDTO forgotPasswordDTO) {
		LOGGER.info("Send OTP");
		// Check email in Data base
		User user = userServiceImpl.getUserByEmail(forgotPasswordDTO.getEmail());
		if (user == null) {
			LOGGER.error("{}: does not exist", forgotPasswordDTO.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
					.body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Email does not exist", null, null));
		}
		String randomOTP = userServiceImpl.randomGenerateOTP();
		// Setup to send email
		EmailDetailsDTO emailDetails = emailServiceImpl.setupEmailDetailsForOTP(forgotPasswordDTO.getEmail(),randomOTP);
		// Setup OTP
		Date dateAfterAdding5Mins = dateExpiredOtp();

		// Check if OTP exist for the user
		OTP getOtpDB = otpRepository.findOtpByUserId(user.getId());
		if (getOtpDB != null) {
			getOtpDB.setOtpNumber(passwordEncoder.encode(randomOTP));
			getOtpDB.setOtpExpiredTime(dateAfterAdding5Mins);
			getOtpDB.setValidOTP(true);
			getOtpDB.setAccessChangePassword(false);
			otpRepository.save(getOtpDB);
		} else {
			// Set up a new OTP
			OTP setUpOtp = OTP.builder()
					.user(user)
					.otpNumber(passwordEncoder.encode(randomOTP))
					.otpExpiredTime(dateAfterAdding5Mins)
					.validOTP(true)
					.accessChangePassword(false)
					.build();
			otpRepository.save(setUpOtp);
		}

		// Send email to user
		boolean checkSendEmail = emailServiceImpl.sendMailNoAttachment(emailDetails);
		if (!checkSendEmail) {
			LOGGER.error("Failed to send mail to: {}", forgotPasswordDTO.getEmail());
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
					.body(new ResponseObject(HttpStatus.FAILED_DEPENDENCY.toString(),
							"Failed to send mail while forgot password", null, forgotPasswordDTO.getEmail()));
		}
		LOGGER.info("Send email success to: {}", forgotPasswordDTO.getEmail());
		OTP otp = otpRepository.findOtpByUserId(user.getId());
		OTP otpId = OTP.builder().id(otp.getId()).build();
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject(HttpStatus.OK.toString(), "Email Sended", null, otpId));
	}

	@Override
	public ResponseEntity<ResponseObject> verifyOTP(OtpVerificationDTO otpVerificationDTO) {
		// Check if OTP exist in Database
		OTP otp = otpRepository.findOtpById(otpVerificationDTO.getOtpId())
				.orElseThrow(() -> new OtpNotFoundException());
		LOGGER.info("Start verify for {}", otp.getUser().getEmail());
		if (!otp.isAccessChangePassword()) {
			// Check if OTP expired
			if (!isExpiredOtp(otp) && !otp.isAccessChangePassword() && otp.isValidOTP()) {
				// OTP verification check
				OTP otpId = OTP.builder().id(otp.getId()).build();
				if (passwordEncoder.matches(otpVerificationDTO.getOtp(), otp.getOtpNumber())) {
					LOGGER.info("Success verification for {}", otp.getUser().getEmail());
					otp.setAccessChangePassword(true);
					otpRepository.save(otp);
					return ResponseEntity.status(HttpStatus.OK)
							.body(new ResponseObject(HttpStatus.OK.toString(), "Valid OTP", null, otpId));
				}
				LOGGER.error("Error otp for {}", otp.getUser().getEmail());
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
						.body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Invalid OTP", null, otpId));
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "OTP expired", null, null));
		}
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new ResponseObject(HttpStatus.METHOD_NOT_ALLOWED.toString(), "Already verify", null, null));
	}

	@Override
	public ResponseEntity<ResponseObject> changePassword(ChangePasswordDTO changePasswordDTO) {
		OTP otp = otpRepository.findOtpById(changePasswordDTO.getOtpId()).orElseThrow(() -> new OtpNotFoundException());
		LOGGER.info("Start change-password for {}", otp.getUser().getEmail());
		// Check if user is verified and allow to change password
		if (otp.isAccessChangePassword()) {
			if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
				LOGGER.error("Error change-password not equal for {}", otp.getUser().getEmail());
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject(
						HttpStatus.NOT_ACCEPTABLE.toString(), "Both passwords do not match", null, null));
			}
			userServiceImpl.setUserPassword(otp.getUser().getEmail(), changePasswordDTO.getNewPassword());
			LOGGER.info("Success change-password for {}", otp.getUser().getEmail());
			otpRepository.deleteById(otp.getId());
			this.clearUserDetailsCache(otp.getUser().getEmail());
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Password successfully updated", null, null));
		}
		LOGGER.info("Access change-password not allow for {}", otp.getUser().getEmail());
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseObject(
				HttpStatus.METHOD_NOT_ALLOWED.toString(), "Please verify before changing password", null, null));
	}

	private boolean isExpiredOtp(OTP otp) {
		Date currentDate = new Date();
		if (currentDate.after(otp.getOtpExpiredTime())) {
			otp.setValidOTP(false);
			otpRepository.save(otp);
			return true;
		}
		return false;
	}

	public Date dateExpiredOtp() {
		// Setup OTP
		Date date = new Date();
		long otpExpiredTimeInMillis = date.getTime();
		// Add 5 minutes to current date
		return new Date(otpExpiredTimeInMillis + 120000);
	}

	private void clearUserDetailsCache(String userEmail) {
		boolean result = cacheManager.getCache("userDetails").evictIfPresent(userEmail);
		if (result) {
			LOGGER.info("Clear account " + userEmail + " from cache");
		} else {
			LOGGER.error("Fail clear account " + userEmail + " from cache");
		}
	}
}
