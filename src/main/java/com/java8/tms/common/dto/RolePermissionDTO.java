package com.java8.tms.common.dto;

import lombok.*;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Setter
public class RolePermissionDTO implements Comparable<RolePermissionDTO> {
    private UUID roleId;
    private String roleName;
    private List<AuthorityDTO> authorities;

    public UUID getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return WordUtils.capitalizeFully(roleName.replace("_"," ").toLowerCase()) ;
    }

    public List<AuthorityDTO> getAuthorities() {
        return authorities;
    }

    @Override
    public int compareTo(RolePermissionDTO o) {
        if (roleId.compareTo(o.roleId) > 0) return 1;
        else if (roleId.compareTo(o.roleId) < 0) return -1;
        return 0;
    }
}
