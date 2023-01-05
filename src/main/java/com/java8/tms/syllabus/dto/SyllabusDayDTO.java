package com.java8.tms.syllabus.dto;

import com.java8.tms.common.meta.SyllabusDayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusDayDTO {
    private UUID id;
    private int dayNo;
    private SyllabusDayStatus status;
    private List<SyllabusUnitDTO> syllabusUnits;

}
