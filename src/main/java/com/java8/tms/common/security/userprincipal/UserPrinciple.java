package com.java8.tms.common.security.userprincipal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.java8.tms.common.entity.Authority;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrinciple implements UserDetails {

    private UUID id;
    private String fullname;
    private String email;
    private Instant expiredDate;
    @JsonIgnore
    private String password;
    private String avatar;
    private String gender;
    private String level;
    private String status;
    private String role;
    private Set<Authority> authorities;

    @JsonIgnore
    private Collection<? extends GrantedAuthority> grantedAuthorities;


    public static UserPrinciple build(User user) {
//        Set<Permission> rolePermission = new PermissionServiceImpl().findByRoles_Name(user.getRole().getName().name());
        List<GrantedAuthority> grantedAuthorities = user.getRole().getAuthorities().stream().map(authority -> new SimpleGrantedAuthority(authority.appendAuthority())).collect(Collectors.toList());
        return UserPrinciple.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole().getName())
                .avatar(user.getAvatar())
                .gender(user.getGender().name())
                .level(user.getLevel())
                .expiredDate(user.getExpiredDate())
                .status(user.getStatus().name())
                .authorities(user.getRole().getAuthorities())
                .grantedAuthorities(grantedAuthorities)
                .build();
    }

    public UUID getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getLevel() {
        return level;
    }

    public Instant getExpiredDate() {
        return expiredDate;
    }
    public Set<Authority> getDefaultAuthorities(){
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        if (expiredDate != null) {
            return expiredDate.compareTo(new Date().toInstant()) > 0;
        }
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !Objects.equals(status, UserStatus.DEACTIVE.name()) && !Objects.equals(status, UserStatus.DELETE.name());
    }
}
