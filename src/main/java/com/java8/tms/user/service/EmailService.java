package com.java8.tms.user.service;

import com.java8.tms.user.dto.EmailDetailsDTO;

public interface EmailService {
	// Method
	// To send a simple email
	boolean sendMailNoAttachment(EmailDetailsDTO details);

	// Method
	// To send an email with attachment
	boolean sendMailWithAttachment(EmailDetailsDTO details);
}
