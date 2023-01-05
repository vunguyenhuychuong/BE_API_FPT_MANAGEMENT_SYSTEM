package com.java8.tms.training_class.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DataExcelForTrainingClass {
    private UUID id;
    private int stt;
    private String courseCode;
    //private LocalDate createdDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private LocalDate startDate;
    private LocalDate endDate;

    private int duration;

    private String locationId; // PK

    private String locationUnit;

    private int noClass;

    private String updatedBy;

    private LocalDateTime updatedDate;

    private String universityCode;

    private String status;

    private String attendeeType;

    private String formatType;

    private String fsu;

    private String technicalGroup;

    private String trainingProgram;

    private String trainingProgramVersion;

    private String programContentId;

    private String recer;

    private int traineeNO;

    private Set<String> trainer;

    private String mentor;

    private Set<String> classAdmin;

    private String formatType_Abb;

    private int classNo_Abb;

    private String universityCode_Abb;

    private int startYear;

    private String MessageError;

    private int plannedAttendee;

}
