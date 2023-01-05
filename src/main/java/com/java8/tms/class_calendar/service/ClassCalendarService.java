package com.java8.tms.class_calendar.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.java8.tms.class_calendar.dto.FilterClassResponse;
import com.java8.tms.class_calendar.dto.ListKeywords;
import com.java8.tms.class_calendar.dto.SearchClassDTO;
import com.java8.tms.common.dto.ClassCalendarDTO;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.TrainingClass;

public interface ClassCalendarService {

    ResponseEntity<ResponseObject> searchTrainingClassCalendar(String search, LocalDateTime date);
	ResponseEntity<ResponseObject> searchTrainingClassCalendar(String search);
	ResponseEntity<ResponseObject> getAllTrainersName();

	ResponseEntity<ResponseObject> filterTrainingClassCalendarByLocation(ClassCalendarDTO dto);

	ResponseEntity<ResponseObject> filterTrainingClassCalendarByStatus(ClassCalendarDTO dto);

	ResponseEntity<ResponseObject> filterAllTrainingClassByAttendee(ClassCalendarDTO dto);

	ResponseEntity<ResponseObject> filterAllTrainingClassByFsu(ClassCalendarDTO dto);

	//ResponseEntity<ResponseObject> filterAllTrainingClassByClassTime(ClassCalendarDTO dto);
	
	//ResponseEntity<ResponseObject> filterAllTrainingClassByTrainer(ClassCalendarDTO dto);
	
	Map<LocalTime, List<SearchClassDTO>> getByDate(String current_date) throws ParseException;
	
	FilterClassResponse getClass(ListKeywords listKeywords);
	
	FilterClassResponse getClassByTrainer(ListKeywords listKeywords);


	ResponseEntity<ResponseObject> getClassById(UUID id);
	ResponseEntity<ResponseObject> getClassName(UUID id);
	ResponseEntity<ResponseObject> getClassCode(UUID id);
	//ResponseEntity<ResponseObject> getCourseUnit(UUID id);
	ResponseEntity<ResponseObject> getClassLocation(UUID id);
	ResponseEntity<ResponseObject> getClassTrainer(UUID id);
	ResponseEntity<ResponseObject> getClassAdmin(UUID id);
	ResponseEntity<ResponseObject> viewInfoClass(UUID id);
}
