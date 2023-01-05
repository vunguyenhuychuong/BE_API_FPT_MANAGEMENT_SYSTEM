package com.java8.tms.training_program.dto;

import com.java8.tms.syllabus.dto.DeliveryTypeDTO;

import com.java8.tms.syllabus.dto.OutputStandardDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnitChapterDTO {
    private UUID id;
    private String name;
    private int duration;
    private boolean isOnline;
    private List<MaterialsDTO> materials;
    private OutputStandardDTO outputStandard;
    private DeliveryTypeDTO deliveryType;
}
