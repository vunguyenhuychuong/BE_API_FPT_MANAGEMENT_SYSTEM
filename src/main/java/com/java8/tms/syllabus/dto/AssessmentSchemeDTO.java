package com.java8.tms.syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSchemeDTO {
    private UUID id;
    private Double assignment;
    private Double quiz;
    private Double exam;
    private Double gpa;
    private Double finalPoint;
    private Double finalTheory;
    private Double finalPractice;
}
