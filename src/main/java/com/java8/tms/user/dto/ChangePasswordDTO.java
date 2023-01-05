package com.java8.tms.user.dto;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
public class ChangePasswordDTO {
	
	@Size(min = 6, message = "Password need to be atleast 6 characters")
	@NotEmpty(message = "Required field!")
	@Schema(description = "Input new password")
	private String newPassword;

	
	@Size(min = 6, message = "Password need to be atleast 6 characters")
	@NotEmpty(message = "Required field!")
	@Schema(description = "Input new password for confirm")
	private String confirmPassword;
	
	@Schema(description = "Input otpId from response here")
	private UUID otpId;
}
