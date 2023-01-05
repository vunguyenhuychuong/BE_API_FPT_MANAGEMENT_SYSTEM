package com.java8.tms.common.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeRoleForm {
    @Schema(description = "user id ", example = "e51ec2b2-a79c-4b5c-89f5-f8412d5f47ae", required = true)
    @NotNull(message = "user id not null")
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", message = "user id is not allow UUID")
    private String userId;

    @Schema(description = "role id to change",example = "9821f52e-4def-11ed-bdc3-0242ac120002", required = true)
    @NotNull(message = "role id not null")
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", message = "role id is not allow UUID")
    private String roleId;
}
