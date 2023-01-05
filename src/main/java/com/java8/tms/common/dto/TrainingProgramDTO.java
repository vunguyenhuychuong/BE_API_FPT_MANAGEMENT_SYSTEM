package com.java8.tms.common.dto;

import com.java8.tms.common.meta.TrainingProgramStatus;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramDTO {
    private UUID id;
    private String name;
    private boolean isTemplate;
    private UUID createdBy;
    private String createdDate;
    private UUID updatedBy;
    private Date updatedDate;
    private String version;
    private TrainingProgramStatus status;

}
