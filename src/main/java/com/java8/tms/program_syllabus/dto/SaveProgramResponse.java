package com.java8.tms.program_syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveProgramResponse {
	private UUID id;
	private String name;
	private String version;
	private String status;
	
	private String nameCreatedBy;
	private Date createdOn;
	
	private int days;
	private int hours;
	
	private SyllabusAndError syllabusData;
}
