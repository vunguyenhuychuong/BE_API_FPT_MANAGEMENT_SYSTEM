package com.java8.tms.program_syllabus.dto;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProgramRequest {
	@NotNull(message = "Id is required")
	private UUID id;
	@Size(min=1,max=10,message ="Program must have between 1-10 syllabuses")
	private List<UUID> syllabuses;
}
