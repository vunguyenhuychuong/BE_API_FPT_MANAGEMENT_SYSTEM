package com.java8.tms.common.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolePermissionsForm {
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String roleId;

    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private List<String> authoritiesId;

    public UUID getRoleId() {
        return UUID.fromString(this.roleId);
    }

    public List<UUID> getAuthoritiesId() {
        return authoritiesId.stream().map(UUID::fromString).collect(Collectors.toList());
    }
}
