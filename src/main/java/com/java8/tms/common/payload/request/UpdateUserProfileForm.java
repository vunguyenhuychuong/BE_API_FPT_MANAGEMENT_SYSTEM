package com.java8.tms.common.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileForm {

    @Schema(description = "user full name",
            example = "Le Van Tien",required = true)
    @Size(max = 60)
    @NotBlank(message = "full name not null")
    private String fullname;

    @Schema(description = "user birth day",
            example = "2001-08-12T17:00:00.000+00:00",required = true)
    private Date birthday;

    @Schema(description = "user gender (EX: male,female)", example = "MALE",required = true)
    @Pattern(regexp = "(male)|(female)|(MALE)|(FEMALE)", message = "gender wrong format (male,female,MALE,FEMALE)")
    private String gender;

}
