package com.java8.tms.common.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdateUserStatusForm {
    @Schema(description = "user id ", example = "87ad5bc2-3bde-439a-97d0-63c4e44d19d9", required = true)
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", message = "user id is not allow UUID")
    @NotBlank(message = "user id not blank")
    private String userId;

    @Schema(description = "status name to change",example = "DEACTIVE", required = true)
    @NotBlank(message = "status name not blank")
    private String statusName;
}
