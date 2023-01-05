package com.java8.tms.class_calendar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java8.tms.common.entity.User;
import com.java8.tms.training_class.dto.TrainingClassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SearchClassDTO {
    @Type(type = "uuid-char")
    private UUID id;
    private int dayNo;
    private int trainingProgramTotalDays;
    private String classCode;
    private String className;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalTime beginTime;
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalTime endTime;
    private String classStatus;
    private String trainingProgramName;
    private String syllabusCode;
    private String syllabusName;


    private int syllabusUnitNo;
    private String syllabusUnitName;
    private String location;
    private Set<User> trainers;
    private Set<User> admins;



}
