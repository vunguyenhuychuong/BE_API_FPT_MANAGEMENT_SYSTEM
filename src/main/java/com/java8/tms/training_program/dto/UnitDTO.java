package com.java8.tms.training_program.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private UUID id;
    private String name;
    private int unitNo;
    private int duration;
    private List<UnitChapterDTO> syllabusUnitChapters;

}
