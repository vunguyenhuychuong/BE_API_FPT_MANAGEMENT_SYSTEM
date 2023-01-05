package com.java8.tms.common.dto;

import java.util.List;
import java.util.Set;

import com.java8.tms.common.entity.ProgramSyllabus;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.common.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingClassDTO {
	private TrainingClass trainingClass;
	
	private String trainingClassCreatedBy;
	private String trainingClassUpdatedBy;
	private String trainingClassReviewedBy;
	private String trainingClassApprovedBy;
	
	private TrainingProgram trainingProgram;
	
	private Set<User> listAdmin;
	private Set<User> listTrainer;
	private List<User> listAttendee;
	
	private String locationName;
	
	private List<ProgramSyllabus> listProgramSyllabus;
	
	private String nameAttendeeLevel;
	private String nameFormatType;
	private String nameClassStatus;
	
	private String nameTechnicalGroup;
	private String nameClassFsu;
}
