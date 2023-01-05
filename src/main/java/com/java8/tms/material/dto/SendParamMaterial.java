package com.java8.tms.material.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendParamMaterial {
	private UUID id;
	private UUID syllabusId;
	private String url;
	private String name;

	
}
