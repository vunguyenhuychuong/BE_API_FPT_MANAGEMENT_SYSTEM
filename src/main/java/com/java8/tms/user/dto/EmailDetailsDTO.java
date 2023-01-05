package com.java8.tms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDetailsDTO {
	private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    private String otp;
	
    public EmailDetailsDTO(String recipient, String msgBody, String subject) {
		super();
		this.recipient = recipient;
		this.msgBody = msgBody;
		this.subject = subject;
	}
    
    public EmailDetailsDTO(String recipient, String msgBody, String subject, String otp) {
    	super();
    	this.recipient = recipient;
    	this.msgBody = msgBody;
    	this.subject = subject;
    	this.otp = otp;
    }
}
