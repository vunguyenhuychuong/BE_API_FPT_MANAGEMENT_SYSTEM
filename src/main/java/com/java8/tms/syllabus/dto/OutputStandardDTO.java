package com.java8.tms.syllabus.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputStandardDTO {
	private UUID id;
	private String name;
	private String code;
	private String description;
}
