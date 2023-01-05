package com.java8.tms.training_program.dto;

import com.java8.tms.syllabus.dto.SyllabusDTO;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainingProgramDetailDTO {
    private UUID id;
    private String name;
    private String createdDate;
    private String createdBy;
    private int duration;
    private int hours;
    private boolean isTemplate;
    private String updatedBy;
    private Date updatedDate;
    private String version;
    private String status;
    private List<SyllabusOfProgramDTO> syllabusOfProgramDTOList;
}
