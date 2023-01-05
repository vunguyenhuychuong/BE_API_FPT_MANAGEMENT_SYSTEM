package com.java8.tms.training_class.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
public class ClassDTO {
    //UUID
    private UUID id;

    //add comment courseCode
    private String courseCode;

    private String trainingProgram;

    private LocalDate startDate;

    private LocalDate endDate;

    private int duration;

    private String attendee;

    private String status;

    private String location;

    private String fsu;
}
