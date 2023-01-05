package com.java8.tms.common.payload.request;

import com.java8.tms.common.dto.AuthorityDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequestForm {


    @Size(min = 2, max = 30)
    @Schema(description = "input new role name",example = "Officer")
    private String roleName;

    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    @Schema(description = "input authorities id")
    private List<String> authoritiesId;

    public String getRoleName() {
        return roleName.toUpperCase().trim().replaceAll(" +", " ").replace(" ", "_");
    }

    public List<UUID> getAuthoritiesId() {
        return authoritiesId.stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName.toUpperCase().trim().replaceAll(" +", " ").replace(" ", "_");
    }

    public void setAuthoritiesId(List<String> authoritiesId) {
        this.authoritiesId = authoritiesId;
    }


}
