package com.java8.tms.user.dto;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupUserDTO {
	@NotEmpty(message = "Required field!")
	@Size(max = 50, message 
    = "Full name must be less than 50 characters")
	@Schema(example = "Toi la admin")
    private String fullName;
	
	@Size(max = 35, message 
		    = "Email must be less than 35 characters")
	@Email(message = "Need to have an email domain")
	@NotEmpty(message = "Required field!")
	@Schema(example = "admin@gmail.com")
    private String email;
	
	@NotEmpty(message = "Required field!")
	@Schema(description = "MALE/FEMALE", example = "MALE")
    private String gender;
    
	@Past(message = "Date must be from past")
	@Schema(description = "yyyy-MM-dd", example = "2000-12-01")
    private Date birthday;
    
    private UUID roleId;
}
