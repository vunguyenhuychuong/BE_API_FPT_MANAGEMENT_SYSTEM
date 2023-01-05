package com.java8.tms.class_calendar.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.java8.tms.class_calendar.dto.FilterClassResponse;
import com.java8.tms.class_calendar.dto.ListKeywords;
import com.java8.tms.class_calendar.dto.SearchClassDTO;
import com.java8.tms.class_calendar.service.impl.ClassCalendarServiceImpl;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.TrainingClass;

@RestController
@RequestMapping(value = "api/v1/training-class/calendar")
public class ClassCalendarController {
	
	@Autowired
    private final ClassCalendarServiceImpl classCalendarService;
	
	@Autowired
    public ClassCalendarController(ClassCalendarServiceImpl classCalendarService) {
        this.classCalendarService = classCalendarService;
    }
	
	@Operation(summary = "get trainning class calendar by date time")
	@PreAuthorize("hasAuthority('VIEW_CLASS')")
	@GetMapping("/get-date")
	public ResponseEntity<ResponseObject> getByDate(@RequestParam String current_date) throws ParseException{
		Map<LocalTime, List<SearchClassDTO>> classes = classCalendarService.getByDate(current_date);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Successful", null, classes));
		
	}
	@Operation(summary = "Filter class calendar")
	@PreAuthorize("hasAuthority('VIEW_CLASS')")
	@PostMapping("/filter-class")
    public FilterClassResponse filterClass(@RequestBody ListKeywords listKeywords) {
        return classCalendarService.getClass(listKeywords);
    }

	@Operation(summary = "Search training class calendar by course code or training program name")
	@PreAuthorize("hasAuthority('VIEW_CLASS')")
	@GetMapping(path =  "/{search}")
	public ResponseEntity<ResponseObject> search(@PathVariable String search){
			return classCalendarService.searchTrainingClassCalendar(search);
	}

	@Operation(summary = "Search training class calendar by course code or training program name in a specific day")
	@PreAuthorize("hasAuthority('VIEW_CLASS')")
	@GetMapping(path = "/{date}/{search}")
	public ResponseEntity<ResponseObject> searchInDay(@PathVariable String date, @PathVariable String search){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime parseDate = LocalDate.parse(date, formatter).atStartOfDay();
			return classCalendarService.searchTrainingClassCalendar(search, parseDate);
	}

	@Operation(summary = "Get all training class calendar")
	@PreAuthorize("hasAuthority('VIEW_CLASS')")
	@GetMapping("/")
	public ResponseEntity<ResponseObject> getAll(){
		return classCalendarService.searchTrainingClassCalendar("");
	}

//	@GetMapping(path = "/viewClassInfo/id={id}")
//	public ResponseEntity<ResponseObject> viewInfoClass(@PathVariable("id") UUID id){
//		return classCalendarService.viewInfoClass(id);
//	}

	@Operation(summary = "Get list of trainers for selection")
	@PreAuthorize("hasAuthority('VIEW_CLASS')")
	@GetMapping("/trainers")
	public ResponseEntity<ResponseObject> getAllTrainers(){
		return classCalendarService.getAllTrainersName();
	}
}
