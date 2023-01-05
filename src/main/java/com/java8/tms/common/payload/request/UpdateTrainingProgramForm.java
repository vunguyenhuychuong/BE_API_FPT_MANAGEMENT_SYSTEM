package com.java8.tms.common.payload.request;

import com.java8.tms.common.meta.TrainingProgramStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder
public class UpdateTrainingProgramForm {
    @Schema(description = "Training program id", example = "2f4b0772-4208-4a74-a3f8-d86b5df0fe4a", required = true)
    @NotBlank(message = "Training program id can not be empty")
    private UUID trainingProgramId;

    @Schema(description = "Training program status", example = "INACTIVE", required = true)
    @NotBlank(message = "Training program status can not be empty")
    private TrainingProgramStatus status;
}
