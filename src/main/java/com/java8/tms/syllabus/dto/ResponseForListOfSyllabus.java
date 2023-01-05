package com.java8.tms.syllabus.dto;

import com.java8.tms.common.meta.SyllabusStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseForListOfSyllabus {
    private UUID id;
    private String name;
    private String code;
    private List<OutputStandardDTO> outputStandardCovered;
    private int days;
    private SyllabusStatus status;
    private UUID createdBy;
    private UserDTO createdByUser;
    private Date createdDate;
    private String version;
}
