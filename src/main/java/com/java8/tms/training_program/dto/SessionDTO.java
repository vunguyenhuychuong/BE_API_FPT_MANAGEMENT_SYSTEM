package com.java8.tms.training_program.dto;

import com.java8.tms.common.meta.SyllabusStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    private UUID id;
    private int dayNo;
    private SyllabusStatus status;
    private List<UnitDTO> syllabusUnits;
}
