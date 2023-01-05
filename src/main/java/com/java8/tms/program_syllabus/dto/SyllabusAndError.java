package com.java8.tms.program_syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyllabusAndError {
	private List<SyllabusResponse> syllabusOk;
	private List<SyllabusErrorResponse> syllabusError;
}
