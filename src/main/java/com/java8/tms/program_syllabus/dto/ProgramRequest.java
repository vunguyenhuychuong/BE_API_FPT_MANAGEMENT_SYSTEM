package com.java8.tms.program_syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramRequest {

	private UUID id;
	@NotBlank(message = "Program Name is Blank")
	@Length(min = 5,max = 100, message="Program Name must be between 5 and 100 character")
	private String name;
	@Size(max=10,message ="Program can not have more than 10 syllabuses")
	private List<UUID> syllabuses;
}
