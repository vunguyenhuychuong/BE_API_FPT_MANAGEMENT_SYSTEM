package com.java8.tms.common.payload.request;

import lombok.*;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterForm {
    @Pattern(regexp = "[a-zA-Z][a-zA-Z ]+", message = "Search name is wrong format")
    private Set<String> searchValue;
    private String gender;
    private Set<String> typeRole;
    private Set<String> status;
    private Date fromBirthday;
    private Date toBirthday;
}
