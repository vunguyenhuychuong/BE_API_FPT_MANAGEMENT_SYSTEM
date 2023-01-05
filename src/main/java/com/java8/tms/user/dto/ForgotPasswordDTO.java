package com.java8.tms.user.dto;

import javax.validation.constraints.Email;
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
public class ForgotPasswordDTO {
	@Size(max = 35, message = "Email must be less than 35 characters")
	@Email(message = "Need to have an email domain")
	@NotEmpty(message = "Required field!")
	@Schema(example = "admin@gmail.com")
    private String email;
	
}
