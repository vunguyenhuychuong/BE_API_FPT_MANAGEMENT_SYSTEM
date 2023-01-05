package com.java8.tms.training_program.dto;

import com.java8.tms.common.meta.SyllabusStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusOfProgramDTO {
    boolean isTemplate;
    private UUID id;
    private String name;
    private String code;
    private String version;
    private int session;
    private int hours;
    private SyllabusStatus status;
    private UUID createdBy;
    private String createdByUser;
    private Date createdDate;
    private UUID updatedBy;
    private String updatedByUser;
    private Date updatedDate;
    private List<SessionDTO> syllabusDays;
}
