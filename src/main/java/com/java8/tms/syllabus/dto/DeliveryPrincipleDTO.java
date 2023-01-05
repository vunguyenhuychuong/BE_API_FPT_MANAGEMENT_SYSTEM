package com.java8.tms.syllabus.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPrincipleDTO {
    private UUID id;
    private String trainees;
    private String trainer;
    private String training;
    private String re_test;
    private String marking;
    private String waiverCriteria;
    private String others;
}
