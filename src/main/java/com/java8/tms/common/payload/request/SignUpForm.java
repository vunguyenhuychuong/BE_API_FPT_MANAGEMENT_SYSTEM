package com.java8.tms.common.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {
    private String fullname;
    private String email;
    private String password;
    private String avatar;
    private String gender;
    private String level;
    private String userStatus;
    private String role;
}
