package com.java8.tms.training_program.dto;

import com.java8.tms.common.meta.MaterialStatus;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MaterialsDTO {
    private UUID id;
    private String name;
    private String url;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private byte[] data;
    private MaterialStatus materialStatus;
}
