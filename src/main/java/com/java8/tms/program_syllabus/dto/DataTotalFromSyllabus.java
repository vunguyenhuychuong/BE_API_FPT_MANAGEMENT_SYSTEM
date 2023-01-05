package com.java8.tms.program_syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataTotalFromSyllabus {
	private int days;
	private int hours;
}
