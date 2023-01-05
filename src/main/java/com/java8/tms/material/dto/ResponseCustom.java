package com.java8.tms.material.dto;

import java.util.UUID;

import com.java8.tms.syllabus.dto.SyllabusDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustom {

	private SyllabusDTO syllabus;
	private UUID previousUnitChapterId;
	
}
