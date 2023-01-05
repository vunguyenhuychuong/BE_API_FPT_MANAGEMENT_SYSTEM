package com.java8.tms.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.OTP;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.repository.OtpRepository;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.user.dto.ChangePasswordDTO;
import com.java8.tms.user.dto.ForgotPasswordDTO;
import com.java8.tms.user.dto.OtpVerificationDTO;
import com.java8.tms.user.service.ForgotPasswordService;
import com.java8.tms.user.service.impl.EmailServiceImpl;
import com.java8.tms.user.service.impl.ForgotPasswordServiceImpl;
import com.java8.tms.user.service.impl.UserServiceImpl;

@SpringBootTest
class ForgotPasswordServiceImplTest {

	@Autowired
	private ForgotPasswordService forgotPasswordService;
	

	@Autowired
	private ForgotPasswordServiceImpl forgotPasswordServiceImp;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private EmailServiceImpl emailServiceImpl;

	@MockBean
	private OtpRepository otpRepository;

	// Set up user id
	private final UUID userId1 = UUID.randomUUID();
	private final UUID userId2 = UUID.randomUUID();
	private final UUID userId3 = UUID.randomUUID();
	private final UUID userId4 = UUID.randomUUID();
	private final UUID userId5 = UUID.randomUUID();
	private final UUID userId6 = UUID.randomUUID();

	// Set up user name
	private final String userName1 = "Nhân Nguyễn";
	private final String userName2 = "Trần Minh Quân";
	private final String userName3 = "Hồ Hải Nam";
	private final String userName4 = "Viên Quốc Bình";
	private final String userName5 = "Cathi Kettow";
	private final String userName6 = "Ivar Stollwerck";

	// Set up email
	private final String email1 = "dnhan2426@gmail.com";
	private final String email2 = "tminhquan999@gmail.com";
	private final String email3 = "honam267@gmail.com";
	private final String email4 = "vienquocbinh@gmail.com";
	private final String email5 = "cathikettow@@gmail.com";
	private final String email6 = "ivarstollwerck@gmail.com";

	// Set up otp id
	private final UUID otpId1 = UUID.randomUUID();
	private final UUID otpId2 = UUID.randomUUID();
	private final UUID otpId3 = UUID.randomUUID();
	private final UUID otpId4 = UUID.randomUUID();
	private final UUID otpId5 = UUID.randomUUID();
	private final UUID otpId6 = UUID.randomUUID();
	// Not found otp
	private final UUID otpId7 = UUID.randomUUID();

	// Set up change password otp id
	private final UUID changePassOtpId1 = UUID.randomUUID();
	private final UUID changePassOtpId2 = UUID.randomUUID();
	private final UUID changePassOtpId3 = UUID.randomUUID();
	private final UUID changePassOtpId4 = UUID.randomUUID();

	private final String subject = "Verification code";

	private final String otp1 = userServiceImpl.randomGenerateOTP();
	private final String otp2 = userServiceImpl.randomGenerateOTP();
	private final String otp3 = userServiceImpl.randomGenerateOTP();
	private final String otp4 = userServiceImpl.randomGenerateOTP();
	private final String otp5 = userServiceImpl.randomGenerateOTP();
	private final String otp6 = userServiceImpl.randomGenerateOTP();
	private final String otp7 = userServiceImpl.randomGenerateOTP();

	@BeforeEach
	void setUp() throws Exception {

		// set up List email, size = 4
		List<ForgotPasswordDTO> emails = new ArrayList<>(List.of(ForgotPasswordDTO.builder().email(email1).build(),
				ForgotPasswordDTO.builder().email(email2).build(), ForgotPasswordDTO.builder().email(email3).build(),
				ForgotPasswordDTO.builder().email(email4).build()));

		// set up List user, size = 6
		List<User> users = new ArrayList<>(List.of(
				User.builder().id(userId1).fullname(userName1).email(email1).build(),
				User.builder().id(userId2).fullname(userName2).email(email2).build(),
				User.builder().id(userId3).fullname(userName3).email(email3).build(),
				User.builder().id(userId4).fullname(userName4).email(email4).build(),
				User.builder().id(userId5).fullname(userName5).email(email5).build(),
				User.builder().id(userId6).fullname(userName6).email(email6).build()));

		Date otpExpiredTime = forgotPasswordServiceImp.dateExpiredOtp();
		
		// set up List otp, size = 6
		List<OTP> otps = new ArrayList<>(List.of(
				OTP.builder().id(otpId1).user(users.get(0)).otpNumber(otp1).otpExpiredTime(otpExpiredTime)
						.validOTP(true).accessChangePassword(true).build(),
				OTP.builder().id(otpId2).user(users.get(1)).otpNumber(otp2).otpExpiredTime(otpExpiredTime)
						.validOTP(true).accessChangePassword(true).build(),
				OTP.builder().id(otpId3).user(users.get(2)).otpNumber(otp3).otpExpiredTime(otpExpiredTime)
						.validOTP(false).accessChangePassword(true).build(),
				OTP.builder().id(otpId4).user(users.get(3)).otpNumber(otp4).otpExpiredTime(otpExpiredTime)
						.validOTP(true).accessChangePassword(false).build(),
				OTP.builder().id(otpId5).user(users.get(4)).otpNumber(otp5).otpExpiredTime(otpExpiredTime)
						.validOTP(false).accessChangePassword(false).build(),
				OTP.builder().id(otpId6).user(users.get(5)).otpNumber(otp6).otpExpiredTime(otpExpiredTime)
						.validOTP(true).accessChangePassword(false).build()));
		// Mockito to email
		Mockito.when(userRepository.getUserByEmail(email1)).thenReturn(users.get(0));
		Mockito.when(userRepository.getUserByEmail(email2)).thenReturn(users.get(1));
		Mockito.when(userRepository.getUserByEmail(email3)).thenReturn(users.get(2));
		Mockito.when(userRepository.getUserByEmail(email4)).thenReturn(users.get(3));

		// set up Optional for otp
		Optional<OTP> otpOptional1 = Optional.ofNullable(otps.get(0));
		Optional<OTP> otpOptional2 = Optional.ofNullable(otps.get(1));
		Optional<OTP> otpOptional3 = Optional.ofNullable(otps.get(2));
		Optional<OTP> otpOptional4 = Optional.ofNullable(otps.get(3));
		Optional<OTP> otpOptional5 = Optional.ofNullable(otps.get(4));
		Optional<OTP> otpOptional6 = Optional.ofNullable(otps.get(5));

		// Mockito to otp
		Mockito.when(otpRepository.findOtpById(otpId1)).thenReturn(otpOptional1);
		Mockito.when(otpRepository.findOtpById(otpId2)).thenReturn(otpOptional2);
		Mockito.when(otpRepository.findOtpById(otpId3)).thenReturn(otpOptional3);
		Mockito.when(otpRepository.findOtpById(otpId4)).thenReturn(otpOptional4);
		Mockito.when(otpRepository.findOtpById(otpId5)).thenReturn(otpOptional5);
		Mockito.when(otpRepository.findOtpById(otpId6)).thenReturn(otpOptional6);
	}

	// Test send mail
	@Test
	void test_sendOTP_When_InvalidEmail_Expect_EmailNotExist() {
		String expectedMsg = "Email does not exist";
		// Input email not found
		ForgotPasswordDTO userEmail = ForgotPasswordDTO.builder().email("nhannd2426@gmail.com").build();
		try {
			ResponseEntity<ResponseObject> sendOtpResponse = forgotPasswordService.sendOTP(userEmail);
			String sendOtpResponseMsg = sendOtpResponse.getBody().getMessage();
			assertEquals(expectedMsg, sendOtpResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

//	@Test
//	void test_sendOTP_When_failSendMailNoAttachment_Expect_FailedToSendMail() {
//		String expectedMsg = "Failed to send mail while forgot password";
//		ForgotPasswordDTO userEmail = ForgotPasswordDTO.builder().email("tminhquan999@gmail.com").build();
//		EmailDetailsDTO emailDetail = emailServiceImpl.setupEmailDetailsForOTP(userEmail.getEmail(), otp1);
////				EmailDetailsDTO.builder().recipient(userEmail.getEmail())
////				.msgBody("Hi " + userEmail.getEmail() + ", here is your otp to reset your password.\n\t\tVerification code: " + otp2)
////				.subject(subject).otp(otp2).build();
//		Mockito.when(emailServiceImpl.sendMailNoAttachment(emailDetail)).thenReturn(false);
//		try {
//			ResponseEntity<ResponseObject> sendOtpResponse = forgotPasswordService.sendOTP(userEmail);
//			String sendOtpResponseMsg = sendOtpResponse.getBody().getMessage();
//			assertEquals(expectedMsg, sendOtpResponseMsg);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}
//	
//	@Test
//	void test_sendOTP_When_SendMailSuccessful_Expect_SendOTPSuccessful() {
//		String expectedMsg = "Email Sended";
//		ForgotPasswordDTO userEmail = ForgotPasswordDTO.builder().email("tminhquan999@gmail.com").build();
//		EmailDetailsDTO emailDetail = EmailDetailsDTO.builder().recipient(userEmail.getEmail())
//				.msgBody("Hi " + userEmail.getEmail() + ", here is your otp to reset your password.\n\t\tVerification code: " + otp2)
//				.subject(subject).otp(otp2).build();
//		Mockito.when(emailServiceImpl.setupEmailDetailsForOTP(userEmail.getEmail(), otp2)).thenReturn(emailDetail);
//		Mockito.when(emailServiceImpl.sendMailNoAttachment(emailDetail)).thenReturn(true);
//		
//		try {
//			ResponseEntity<ResponseObject> sendOtpResponse = forgotPasswordService.sendOTP(userEmail);
//			String sendOtpResponseMsg = sendOtpResponse.getBody().getMessage();
//			assertEquals(expectedMsg, sendOtpResponseMsg);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}

	// Test verify OTP
	@Test
	void test_verifyOTP_When_OtpIdNotFound_Expect_OtpNotFoundException() {
		String expectedMsg = "OTP not found";
		// Otp not found and verify otp not successful
		OtpVerificationDTO otpVerified = OtpVerificationDTO.builder().otp(otp5).otpId(otpId5).build();
		try {
			ResponseEntity<ResponseObject> sendOtpResponse = forgotPasswordService.verifyOTP(otpVerified);
		} catch (Exception e) {
			assertEquals(expectedMsg, e.getMessage());
		}
	}

	@Test
	void test_verifyOTP_When_isAccessChangePasswordEqualFalse_Expect_AlreadyVerify() {
		String expectedMsg = "Already verify";
		// Otp has already verify and verify otp not successful
		OtpVerificationDTO otpVerified = OtpVerificationDTO.builder().otp(otp3).otpId(otpId3).build();
		try {
			ResponseEntity<ResponseObject> verifyOtpResponse = forgotPasswordService.verifyOTP(otpVerified);
			String verifyOtpResponseMsg = verifyOtpResponse.getBody().getMessage();
			assertEquals(expectedMsg, verifyOtpResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_verifyOTP_When_ExpiredOtp_Expect_OtpExpired() {
		String expectedMsg = "OTP expired";
		// Otp is expired and verify otp not successful
		OtpVerificationDTO otpVerified = OtpVerificationDTO.builder().otp(otp5).otpId(otpId5).build();
		try {
			ResponseEntity<ResponseObject> verifyOtpResponse = forgotPasswordService.verifyOTP(otpVerified);
			String verifyOtpResponseMsg = verifyOtpResponse.getBody().getMessage();
			assertEquals(expectedMsg, verifyOtpResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void test_verifyOTP_When_InvalidOtp_Expect_VerifyOtpUnsuccessful() {
		String expectedMsg = "Invalid OTP";
		// Input valid otp and verify otp successful
		OtpVerificationDTO otpVerified = OtpVerificationDTO.builder().otp("X6K0J6").otpId(otpId6).build();
		try {
			ResponseEntity<ResponseObject> verifyOtpResponse = forgotPasswordService.verifyOTP(otpVerified);
			String verifyOtpResponseMsg = verifyOtpResponse.getBody().getMessage();
			assertEquals(expectedMsg, verifyOtpResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void test_verifyOTP_When_ValidOtp_Expect_VerifyOtpSuccessful() {
		String expectedMsg = "Valid OTP";
		// Input valid otp and verify otp successful
		OtpVerificationDTO otpVerified = OtpVerificationDTO.builder().otp(otp6).otpId(otpId6).build();
		try {
			ResponseEntity<ResponseObject> verifyOtpResponse = forgotPasswordService.verifyOTP(otpVerified);
			String verifyOtpResponseMsg = verifyOtpResponse.getBody().getMessage();
			assertEquals(expectedMsg, verifyOtpResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	// Test change new password
	@Test
	void test_changePassword_When_OtpIdNotFound_Expect_OtpNotFoundException() {
		String expectedMsg = "OTP not found";
		// Otp not found and not allow to change password
		ChangePasswordDTO changePass = ChangePasswordDTO.builder().newPassword("abc123").confirmPassword("abc123")
				.otpId(otpId7).build();
		try {
			ResponseEntity<ResponseObject> sendOtpResponse = forgotPasswordService.changePassword(changePass);
		} catch (Exception e) {
			assertEquals(expectedMsg, e.getMessage());
		}
	}

	@Test
	void test_changePassword_When_OtpNotVerified_Expect_VerifyBeforeChangingPassword() {
		String expectedMsg = "Please verify before changing password";
		// User is not verified and not allow to change password
		ChangePasswordDTO changePass = ChangePasswordDTO.builder().newPassword("abc123").confirmPassword("abc123")
				.otpId(otpId4).build();
		try {
			ResponseEntity<ResponseObject> ChangingPasswordResponse = forgotPasswordService.changePassword(changePass);
			String ChangingPasswordResponseMsg = ChangingPasswordResponse.getBody().getMessage();
			assertEquals(expectedMsg, ChangingPasswordResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_changePassword_When_ConfirmPassNotEqual_Expect_BothPasswordsNotMatch() {
		String expectedMsg = "Both passwords do not match";
		// Input confirm password not match with new password and not allow to change
		// password
		ChangePasswordDTO changePass = ChangePasswordDTO.builder().newPassword("abc123").confirmPassword("abc456")
				.otpId(otpId1).build();
		try {
			ResponseEntity<ResponseObject> ChangingPasswordResponse = forgotPasswordService.changePassword(changePass);
			String ChangingPasswordResponseMsg = ChangingPasswordResponse.getBody().getMessage();
			assertEquals(expectedMsg, ChangingPasswordResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_changePassword_When_ChangePassSuccessful_Expect_PasswordSuccessfullyUpdated() {
		String expectedMsg = "Password successfully updated";
		// Valid input and allow to change password
		ChangePasswordDTO changePass = ChangePasswordDTO.builder().newPassword("abc123").confirmPassword("abc123")
				.otpId(otpId2).build();
		try {
			ResponseEntity<ResponseObject> ChangingPasswordResponse = forgotPasswordService.changePassword(changePass);
			String ChangingPasswordResponseMsg = ChangingPasswordResponse.getBody().getMessage();
			assertEquals(expectedMsg, ChangingPasswordResponseMsg);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
