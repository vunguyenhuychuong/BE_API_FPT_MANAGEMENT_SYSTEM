package com.java8.tms.syllabus.dto;

import com.java8.tms.common.meta.MaterialStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {
    private UUID id;
    private String name;
    private String url;
    private UUID createdBy;
    private UserDTO createdByUser;
    private Date createdDate;
    private UUID updatedBy;
    private UserDTO updatedByUser;
    private Date updatedDate;
    private byte[] data;
    private MaterialStatus materialStatus;
}
