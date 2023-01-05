package com.java8.tms.class_calendar.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ListKeywords {
	private List<String> location;
	private List<String> status;
	private List<String> attendee;
	
	
	@JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate from;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate to;
    
    private List<String> classTime;
	private String fsu;
	private String trainer;
}
