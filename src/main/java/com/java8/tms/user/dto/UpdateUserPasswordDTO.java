package com.java8.tms.user.dto;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPasswordDTO {
	
	@Size(min = 6, message = "Password need to be atleast 6 characters")
	@NotEmpty(message = "Required field!")
	private String currentPassword;
	
	@Size(min = 6, message = "Password need to be atleast 6 characters")
	@NotEmpty(message = "Required field!")
	private String newPassword;

	
	@Size(min = 6, message = "Password need to be atleast 6 characters")
	@NotEmpty(message = "Required field!")
	private String confirmNewPassword;
}