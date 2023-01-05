package com.java8.tms.common.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassCalendarDTO {
	private UUID id;
	
	private String name;
	
	private String location;
	
	private String status;
	
	private String attendee;
	
	private String fsu;
	
//	private Date classTimeFrom;
//	
//	private Date classTimeTo;
	
	private Date startDate;
	
	private Date endDate;
	
	private LocalDate startTime;
	
	private LocalDate endTime;
	
	private String trainer;
	
    private String nameFormatType;
    
    private TrainingClass trainingClass;
    
    private String className;
    
	private String classCode;
	
	private Set<User> listAdmin;
	
	private Set<User> listTrainer;
	
	private String classTime;
	
	private Date classDate;
	
}
