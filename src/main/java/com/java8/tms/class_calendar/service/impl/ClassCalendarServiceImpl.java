package com.java8.tms.class_calendar.service.impl;

import com.java8.tms.class_calendar.dto.FilterClassDTO;
import com.java8.tms.class_calendar.dto.FilterClassResponse;
import com.java8.tms.class_calendar.dto.ListKeywords;
import com.java8.tms.class_calendar.dto.SearchClassDTO;
import com.java8.tms.class_calendar.service.ClassCalendarService;
import com.java8.tms.common.dto.ClassCalendarDTO;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.*;
import com.java8.tms.common.repository.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ClassCalendarServiceImpl implements ClassCalendarService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClassCalendarRepository classCalendarRepository;
	@Autowired
	private ClassLocationRepository classLocationRepository;

	@Autowired
	private ClassStatusRepository classStatusRepository;

	@Autowired
	private TrainingClassRepository trainingClassRepository;

	@Autowired
	private FSURepository fsuRepository;

	@Autowired
	private AttendeeLevelRepository attendeeLevelRepository;

	public AttendeeLevel getAttendeeByName(String attendeeLevelName) {
		return attendeeLevelRepository.findByName(attendeeLevelName);
	}

	public ClassStatus getClassStatusByName(String classStatusName) {
		return classStatusRepository.findByName(classStatusName);
	}

	public ClassLocation getClassLocationByName(String classLocationName) {
		return classLocationRepository.findByName(classLocationName);
	}

	@Override
	public ResponseEntity<ResponseObject> searchTrainingClassCalendar(String search, LocalDateTime dateTime) {

		List<ClassCalendar> list = classCalendarRepository
				.findByDateTimeAndTrainingClass_CourseCodeOrTrainingClass_TrainingProgram_Name(dateTime,
						'%' + search.toUpperCase() + '%', '%' + search.toUpperCase() + '%');
		List<SearchClassDTO> classesDTO = convertClassCalendarToClassCalendarDTO(list);
		Map<LocalTime, List<SearchClassDTO>> listClassesDTO = mapListSearchDTOByBeginTime(classesDTO);
		ResponseEntity<ResponseObject> response = ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject(HttpStatus.OK.toString(), "Successfully!", null, listClassesDTO));
		return response;
	}

	@Override
	public ResponseEntity<ResponseObject> searchTrainingClassCalendar(String search) {

		List<ClassCalendar> list = classCalendarRepository
				.findByTrainingClass_CourseCodeOrTrainingClass_TrainingProgram_Name('%' + search.toUpperCase() + '%',
						'%' + search.toUpperCase() + '%');
		List<SearchClassDTO> classesDTO = convertClassCalendarToClassCalendarDTO(list);
		ResponseEntity<ResponseObject> response = ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject(HttpStatus.OK.toString(), "Successfully!", null, classesDTO));

		return response;
	}



	@Override
	public ResponseEntity<ResponseObject> filterTrainingClassCalendarByLocation(ClassCalendarDTO dto) {
		String location = dto.getLocation();
		ClassLocation classLocation = classLocationRepository.findByName(location);
		if (classLocation != null) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successful", null,
					trainingClassRepository.findAllTrainingClassByLocation(classLocation)));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseObject("Fails", "Unsuccessful, not found class", null, null));
	}

	@Override
	public ResponseEntity<ResponseObject> filterTrainingClassCalendarByStatus(ClassCalendarDTO dto) {
		String status = dto.getStatus();
		ClassStatus classStatus = classStatusRepository.findByName(status);
		if (classStatus != null) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successful", null,
					trainingClassRepository.findAllTrainingClassByStatus(classStatus)));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseObject("Fails", "Unsuccessful, not found class", null, null));
	}

	@Override
	public ResponseEntity<ResponseObject> filterAllTrainingClassByAttendee(ClassCalendarDTO dto) {
		String attendee = dto.getAttendee();
		AttendeeLevel attendeeLevel = attendeeLevelRepository.findByName(attendee);
		if (attendeeLevel != null) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successful", null,
					trainingClassRepository.findAllTrainingClassByAttendee(attendeeLevel)));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseObject("Fails", "Unsuccessful, not found class", null, null));
	}

	@Override
	public ResponseEntity<ResponseObject> filterAllTrainingClassByFsu(ClassCalendarDTO dto) {
		String fsu = dto.getFsu();
		FSU fsuName = fsuRepository.findByName(fsu);
		if (fsuName != null) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successful", null,
					trainingClassRepository.findAllTrainingClassByFsu(fsuName)));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseObject("Fails", "Unsuccessful, not found class", null, null));
	}

	@Override
	public FilterClassResponse getClass(ListKeywords listKeywords) {
		FilterClassResponse filterClassResponse = new FilterClassResponse();
		List<TrainingClass> listTrainingClass = trainingClassRepository.findAll();
		if (listTrainingClass != null) {
			List<FilterClassDTO> data = listTrainingClass.stream()
					.map(trainingClass -> mapToClassFilterDTO(trainingClass)).collect(Collectors.toList());
			List<FilterClassDTO> list = getClassResponse(data, listKeywords.getLocation(), listKeywords.getFrom(),
					listKeywords.getTo(), listKeywords.getClassTime(), listKeywords.getStatus(),
					listKeywords.getAttendee(), listKeywords.getFsu(), listKeywords.getTrainer());
			filterClassResponse.setStatus("OK");
			filterClassResponse.setMessage("Sucessful");
			filterClassResponse.setData(list);
		} else {
			filterClassResponse.setStatus("Fails");
			filterClassResponse.setMessage("Not found");
		}
		return filterClassResponse;
	}

	@Override
	public FilterClassResponse getClassByTrainer(ListKeywords listKeywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<ResponseObject> getClassById(UUID id) {
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "successful", null, trainingClass));
	}

	@Override
	public ResponseEntity<ResponseObject> getClassName(UUID id) {
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class name by id", null, null));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "successful", null, trainingClass.getTrainingProgram().getName()));
	}

	@Override
	public ResponseEntity<ResponseObject> getClassCode(UUID id) {
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class name by id", null, null));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "successful", null, trainingClass.getCourseCode()));
	}

	@Override
	public ResponseEntity<ResponseObject> getClassLocation(UUID id) {
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class name by id", null, null));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "successful", null, trainingClass.getClassLocation()));
	}

	@Override
	public ResponseEntity<ResponseObject> getClassTrainer(UUID id) {
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class name by id", null, null));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "successful", null, trainingClass.getAccount_trainers()));
	}

	@Override
	public ResponseEntity<ResponseObject> getClassAdmin(UUID id) {
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class name by id", null, null));
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "successful", null, trainingClass.getAccount_admins()));
	}

	@Override
	public ResponseEntity<ResponseObject> viewInfoClass(UUID id) {
		ClassCalendarDTO classCalendarDTO = new ClassCalendarDTO();
		TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
		if (trainingClass == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseObject("Not Found", "Unsuccessful, Not found class name by id", null, null));
		}
		classCalendarDTO.setTrainingClass(trainingClass);
		classCalendarDTO.setClassName((String) getClassName(id).getBody().getData());
		classCalendarDTO.setClassCode((String) getClassCode(id).getBody().getData());
		classCalendarDTO.setLocation((String) getClassLocation(id).getBody().getData());
		classCalendarDTO.setListAdmin((Set<User>) getClassAdmin(id).getBody().getData());
		classCalendarDTO.setListTrainer((Set<User>) getClassTrainer(id).getBody().getData());

		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "successful", null, classCalendarDTO));
	}
	
	
	private List<FilterClassDTO> getClassResponse(List<FilterClassDTO> listClass, List<String> location, 
													LocalDate from, LocalDate to, List<String> classTime, List<String> status,
													List<String> attendee, String fsu, String trainer){
		List<FilterClassDTO> tmpList = new ArrayList<>();
		
		if (location != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				for (String key : location) {
					if (value.getLocation().contains(key)) {
						check = true;
					}
					if (check == false && !tmpList.contains(value))
						tmpList.add(value);
				}
			}
		}

		if (from != null && to != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				if(value.getStartDate().isAfter(from) && value.getStartDate().isBefore(to) ||
						value.getEndDate().isAfter(from) && value.getEndDate().isBefore(to)) {
					check = true;
				}
				if (check == false && !tmpList.contains(value))
					tmpList.add(value);
			}
		}

		if (classTime != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				boolean notClassTime = true;
				boolean notFormatType = true;
				for (String key : classTime) {
					try {
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss");
						notClassTime = true;
						notFormatType = true;
						LocalTime fromClassTime = (LocalTime) dtf.parse("00:00:00");
						LocalTime toClassTime = (LocalTime) dtf.parse("00:00:00");
						String formatType = "";
						switch (key) {
							case "Morning":
								fromClassTime = (LocalTime) dtf.parse("08:00:00");
								toClassTime = (LocalTime) dtf.parse("12:00:00");
								notClassTime = false;
								break;
							case "Noon":					
								fromClassTime = (LocalTime) dtf.parse("13:00:00");
								toClassTime = (LocalTime) dtf.parse("17:00:00");
								notClassTime = false;
								break;
							case "Afternoon":					
								fromClassTime = (LocalTime) dtf.parse("18:00:00");
								toClassTime = (LocalTime) dtf.parse("21:00:00");
								notClassTime = false;
								break;
						}
						
						if(notClassTime == false) {
							if(value.getStartTime().isAfter(fromClassTime) && value.getEndTime().isBefore(toClassTime)) {
								check = true;
							}
						}

						if (notFormatType == false) {
							if (value.getNameFormatType().equals(formatType)) {
								check = true;
							}
						}
					} catch (Exception e) {
					}
				}
				if (check == false && !tmpList.contains(value)) {
					tmpList.add(value);
				}
			}
		}

		if (status != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				for (String key : status) {
					if (value.getStatus().contains(key)) {
						check = true;
					}
				}
				if (check == false && !tmpList.contains(value)) {
					tmpList.add(value);
				}
			}
		}

		if (attendee != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				for (String key : attendee) {
					if (value.getAttendeeName().contains(key)) {
						check = true;
					}
				}
				if (check == false && !tmpList.contains(value)) {
					tmpList.add(value);
				}
			}
		}

		if (fsu != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				if (value.getFsu().contains(fsu)) {
					check = true;
				}
				if (check == false && !tmpList.contains(value)) {
					tmpList.add(value);
				}
			}
		}

		if (trainer != null) {
			for (FilterClassDTO value : listClass) {
				boolean check = false;
				for (User key : value.getTrainer()) {
					if (key.getFullname().contains(trainer)) {
						check = true;
					}
					if (check == false && !tmpList.contains(value)) {
						tmpList.add(value);
					}
				}
			}
		}

		for (FilterClassDTO value : tmpList) {
			listClass.remove(value);
		}

		return listClass;
	}

	private FilterClassDTO mapToClassFilterDTO(TrainingClass trainingClass) {
		FilterClassDTO filterClassDTO = new FilterClassDTO();
		
		filterClassDTO.setId(trainingClass.getId());
		filterClassDTO.setNameTrainingProgram(trainingClass.getName());
		filterClassDTO.setAttendeeName(trainingClass.getAttendeeLevel().getName());
		filterClassDTO.setCourseCode(trainingClass.getCourseCode());
		filterClassDTO.setLocation(trainingClass.getClassLocation().getName());
		filterClassDTO.setStartDate(trainingClass.getStartDate());
		filterClassDTO.setEndDate(trainingClass.getEndDate());
		filterClassDTO.setStartTime(trainingClass.getStartTime());
		filterClassDTO.setEndTime(trainingClass.getEndTime());
		filterClassDTO.setStatus(trainingClass.getClassStatus().getName());
		filterClassDTO.setFsu(trainingClass.getFsu().getName());
		filterClassDTO.setTrainer(trainingClass.getAccount_trainers());
		filterClassDTO.setNameFormatType(trainingClass.getFormatType().getName());
		return filterClassDTO;
	}

	@Override
	public Map<LocalTime, List<SearchClassDTO>> getByDate(String current_date) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime created_Date = LocalDateTime.parse(current_date, formatter);
		List<ClassCalendar> classes = classCalendarRepository.findByDateTime(created_Date);
		List<SearchClassDTO> classesDTO = convertClassCalendarToClassCalendarDTO(classes);
		Map<LocalTime, List<SearchClassDTO>> listClassesDTO = new HashMap<>();
		List<SearchClassDTO> classesRes = new ArrayList<>();
			if (classesDTO.size() == 0) {
				return null;
			}
			if (classesDTO.size() == 1) {
				listClassesDTO.put(classesDTO.get(0).getBeginTime(), classesDTO);
				return listClassesDTO;
			}

		for (int i = 0; i < classesDTO.size(); i++) {
			if(!listClassesDTO.containsKey(classesDTO.get(i).getBeginTime())) {
				listClassesDTO.put(classesDTO.get(i).getBeginTime(), new ArrayList<>());
			}
		}
		for (int i = 0; i < classesDTO.size(); i++) {
			for (LocalTime listKey : listClassesDTO.keySet()) {
				if(listKey.equals(classesDTO.get(i).getBeginTime())) {
					listClassesDTO.get(listKey).add(classesDTO.get(i));
					break;
				}
			}
		}
		return listClassesDTO;
	}

	private List<SearchClassDTO> convertClassCalendarToClassCalendarDTO(List<ClassCalendar> list) {
		List<SearchClassDTO> listDTO = new ArrayList<>();
		for (ClassCalendar c : list) {
			List<ProgramSyllabus> programSyllabusList = c.getTrainingClass().getTrainingProgram()
					.getProgramSyllabusAssociation();
			int trainingProgramTotalDays = 0;
			Syllabus currentClassSyllabus = new Syllabus();
			SyllabusUnit classUnit = new SyllabusUnit();
			if (!programSyllabusList.isEmpty()) {
				currentClassSyllabus = programSyllabusList.get(0).getSyllabus();
				int currentClassSyllabusPosition = 0;
				for (ProgramSyllabus programSyllabus : programSyllabusList) {
					if (c.getDay_no() >= programSyllabus.getSyllabus().getDays()) {
						currentClassSyllabusPosition = programSyllabus.getPosition();
						currentClassSyllabus = programSyllabus.getSyllabus();
					}
					trainingProgramTotalDays += programSyllabus.getSyllabus().getDays();
				}
				int currentClassSyllabusDayNo = 0;
				for (int i = 0; i < currentClassSyllabusPosition; i++) {
					if (currentClassSyllabusPosition == 1) {
						currentClassSyllabusDayNo = c.getDay_no();
					} else {
						currentClassSyllabusDayNo = c.getDay_no() - programSyllabusList.get(i).getSyllabus().getDays();
					}
				}
				List<SyllabusUnit> listUnits = currentClassSyllabus.getSyllabusUnits();
				int s = 0;
				classUnit = new SyllabusUnit();
				for (SyllabusUnit unit : listUnits) {
					s += unit.getDuration();
					if (currentClassSyllabusDayNo <= s) {
						classUnit = unit;
						break;
					}
				}
			}







//			s = 0;
//			List<SyllabusUnitChapter> unitChapterList = classUnit.getSyllabusUnitChapters();
//			for (SyllabusUnitChapter unitChapter: unitChapterList){
//				s += uni
//			}

			listDTO.add(new SearchClassDTO(c.getId(), c.getDay_no(), trainingProgramTotalDays, c.getTrainingClass().getCourseCode(), c.getTrainingClass().getName(), c.getDateTime(),
					c.getBeginTime(), c.getEndTime(), c.getTrainingClass().getClassStatus().getName(),
					c.getTrainingClass().getTrainingProgram().getName(), currentClassSyllabus.getCode(),
					currentClassSyllabus.getName(), classUnit.getUnitNo(), classUnit.getName(),
					c.getTrainingClass().getClassLocation().getName(), c.getTrainingClass().getAccount_trainers(),
					c.getTrainingClass().getAccount_admins()));
		}
		return listDTO;
	}

	private Map<LocalTime, List<SearchClassDTO>> mapListSearchDTOByBeginTime(List<SearchClassDTO> classesDTO) {
		Map<LocalTime, List<SearchClassDTO>> listClassesDTO = new HashMap<>();
		for (int i = 0; i < classesDTO.size(); i++) {
			if(!listClassesDTO.containsKey(classesDTO.get(i).getBeginTime())) {
				listClassesDTO.put(classesDTO.get(i).getBeginTime(), new ArrayList<>());
			}
		}
		for (int i = 0; i < classesDTO.size(); i++) {
			for (LocalTime listKey : listClassesDTO.keySet()) {
				if(listKey.equals(classesDTO.get(i).getBeginTime())) {
					listClassesDTO.get(listKey).add(classesDTO.get(i));
					break;
				}
			}
		}
		return listClassesDTO;
	}

	@Override
	public ResponseEntity<ResponseObject> getAllTrainersName() {
		ResponseEntity<ResponseObject> response = ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject(HttpStatus.OK.toString(), "Successfully!", null, userRepository.findAllByRole_Name("TRAINER")));

		return response;
	}
}
