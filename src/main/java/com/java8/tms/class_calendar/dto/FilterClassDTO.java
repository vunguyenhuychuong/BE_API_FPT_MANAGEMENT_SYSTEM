package com.java8.tms.class_calendar.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java8.tms.common.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterClassDTO {
	private UUID id;
	
	private String courseCode;
	
	private String nameTrainingProgram;
	
	private String attendeeName;
	
	private String status;
	
	private String location;
	
	private String fsu;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private LocalDate startDate;
	
	@JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;
    
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalTime endTime;
    private Set<User> trainer;
    

    private String nameFormatType;
}
