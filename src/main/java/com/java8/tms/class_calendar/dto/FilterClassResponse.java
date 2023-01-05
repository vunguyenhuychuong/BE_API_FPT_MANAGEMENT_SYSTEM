package com.java8.tms.class_calendar.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterClassResponse {
	private String status;
	private String message;
	private List<FilterClassDTO> data;
}
