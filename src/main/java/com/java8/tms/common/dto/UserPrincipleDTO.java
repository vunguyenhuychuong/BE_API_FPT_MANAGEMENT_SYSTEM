package com.java8.tms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserPrincipleDTO {
    private UUID id;
    private String fullname;
    private String email;
    private String avatar;
    private String gender;
    private String level;
    private String status;
    private String role;
    private Set<AuthorityWithoutIdDTO> authorities;

    public UUID getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getGender() {
        return gender;
    }

    public String getLevel() {
        return level;
    }

    public String getStatus() {
        return status.toString() ;
    }

    public String getRole() {
       return WordUtils.capitalizeFully(role.replace("_"," ").toLowerCase()) ;
    }

    public Set<AuthorityWithoutIdDTO> getAuthorities() {
        return authorities;
    }
}
