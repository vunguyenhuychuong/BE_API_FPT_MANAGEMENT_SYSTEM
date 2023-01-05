package com.java8.tms.training_class.dto;

import com.java8.tms.common.entity.*;
import lombok.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TrainingClassUpdateDTO {

    private UUID id;

    private String name;

    private String courseCode;

    private String startTime; // time frame

    private String endTime; // time frame

    private String startDate; // plan start date

    private String endDate; // plan end date

    private int duration; // month

    private String createdBy;

    private String createdDate;

    private String updatedBy;

    private String updatedDate;

    private String reviewedBy;

    private String reviewedDate;

    private String approvedBy;

    private String approvedDate;

    private String universityCode;

    private int plannedAttendee;

    private int acceptedAttendee;

    private int actualAttendee;

    private String classLocation;

    private String attendeeLevel;

    private String formatType;

    private String classStatus;

    private String technicalGroup;

    private String programContent;

    private String fsu;

    private TrainingProgram trainingProgram;

    private Set<User> account_trainers;

    private Set<User> account_admins;

    private List<User> account_trainee;

}
