package com.java8.tms.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.java8.tms.user.dto.EmailDetailsDTO;
import com.java8.tms.user.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sender;

	/**
	 * 
	 * <p>
	 * Setting up a new EmailDetails for sending email
	 * </p>
	 *
	 * @param email
	 * @param password
	 * @param fullName
	 * @return EmailDetailsDTO
	 *
	 * @author Minh Quan
	 */
	public EmailDetailsDTO setUpEmailDetailsForSignup(String email, String password, String fullName) {
		String sendPasswordEmailBodyMsg = "Hi " + fullName + "\nHere is your login account.\n\tEmail: " + email
				+ "\n\tPassword: " + password + "\nThis is a message from the system, please do not reply."
				+ "\nThank you and Warmest Regards.\nFPT Software Academy\n"
				+ "G Floor, F-Town 1 Building, High-tech Park, Tan Phu Ward, Thu Duc City, Ho Chi Minh City, Vietnam\n"
				+ "T  +84 353 624 654            W   fsoft-academy.edu.vn\n\n\n\n"
				+ "***********************************************************************\n" + "IMPORTANT NOTICE\n"
				+ "This email may contain confidential and/ or privileged information that belongs to FPT Software. If you are not the intended recipient or might have received this email by accident from an unreliable source, please notify the sender from FPT Software immediately and destroy this email. Keep in mind that any unauthorized copying, editing, disclosure or distribution of the material in this email is strictly forbidden, plus against the law by which FPT Software and involved clients abide.";
		return new EmailDetailsDTO(email, sendPasswordEmailBodyMsg, "Sign-in account");
	}

	public EmailDetailsDTO setupEmailDetailsForOTP(String email, String otp) {
		String subject = "Verification code";
		String sendOTP = "Hi " + email + ", here is your otp to reset your password.\n\t\tVerification code: " + otp
				+ "\nPlease verify in 2 minutes\nThis is a message from the system, please do not reply\n"
				+ "\nThank you and Warmest Regards.\nFPT Software Academy\n"
				+ "G Floor, F-Town 1 Building, High-tech Park, Tan Phu Ward, Thu Duc City, Ho Chi Minh City, Vietnam\n"
				+ "T  +84 353 624 654            W   fsoft-academy.edu.vn\n\n\n\n"
				+ "***********************************************************************\n" + "IMPORTANT NOTICE\n"
				+ "This email may contain confidential and/ or privileged information that belongs to FPT Software. If you are not the intended recipient or might have received this email by accident from an unreliable source, please notify the sender from FPT Software immediately and destroy this email. Keep in mind that any unauthorized copying, editing, disclosure or distribution of the material in this email is strictly forbidden, plus against the law by which FPT Software and involved clients abide.";
		return new EmailDetailsDTO(email, sendOTP, subject, otp);
	}

	@Override
	public boolean sendMailNoAttachment(EmailDetailsDTO details) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			mailMessage.setFrom(sender);
			mailMessage.setTo(details.getRecipient());
			mailMessage.setText(details.getMsgBody());
			mailMessage.setSubject(details.getSubject());

			javaMailSender.send(mailMessage);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean sendMailWithAttachment(EmailDetailsDTO details) {

		return false;
	}

}
