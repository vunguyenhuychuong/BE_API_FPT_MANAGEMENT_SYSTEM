package com.java8.tms.common.dto;

import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class UserDTO {
    private UUID id;
    private String fullname;
    private String email;
    private Date birthday;
    private String avatar;
    private Gender gender;
    private String level;
    private UserStatus status;
    private RoleWithoutAuthorDTO role;
    private FSUDTO fsu;

    public UUID getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public Gender getGender() {
        return gender;
    }

    public String getLevel() {
        return level;
    }

    public String getStatus() {
        return status.toString();
    }

    public RoleWithoutAuthorDTO getRole() {
        return role;
    }

    public FSUDTO getFsu() {
        return fsu;
    }
}

