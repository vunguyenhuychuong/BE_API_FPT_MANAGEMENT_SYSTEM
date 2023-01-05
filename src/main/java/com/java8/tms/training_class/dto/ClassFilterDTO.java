package com.java8.tms.training_class.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java8.tms.common.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.Set;
import java.util.UUID;


@Data
public class ClassFilterDTO {

     private UUID id;

     private String courseCode;

     private String nameTrainingProgram;

     private int duration;

     private String nameAttendee;

     private String nameStatus;

     private String nameLocation;

     private String nameFsu;

     private Date startDate;

     private Date endDate;
     @JsonFormat(pattern = "hh:mm")
     private Date startTime;
     @JsonFormat(pattern = "hh:mm")
     private Date endTime;

     private Set<User> trainer;

     private String nameFormatType;
}
