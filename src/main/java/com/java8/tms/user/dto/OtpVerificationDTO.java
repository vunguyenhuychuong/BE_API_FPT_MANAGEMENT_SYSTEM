package com.java8.tms.user.dto;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class OtpVerificationDTO {
	@NotEmpty(message = "Required field!") 
	@Schema(example = "A1BD78")
	private String otp;
	
	@Schema(description = "Input otpId from response here")
	private UUID otpId;
}
