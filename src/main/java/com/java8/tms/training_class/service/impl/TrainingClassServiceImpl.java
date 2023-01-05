package com.java8.tms.training_class.service.impl;

import com.java8.tms.attendee_level.service.AttendeeLevelService;
import com.java8.tms.class_location.service.ClassLocationService;
import com.java8.tms.class_status.service.ClassStatusService;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.TrainingClassDTO;
import com.java8.tms.common.entity.*;
import com.java8.tms.common.exception.CustomExceptionHandler;
import com.java8.tms.common.exception.ResourceNotFoundException;
import com.java8.tms.common.payload.request.UpdateClassForm;
import com.java8.tms.common.repository.*;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.common.utils.DateTimeUtils;
import com.java8.tms.format_type.service.FormatTypeService;
import com.java8.tms.fsu.service.FSUService;
import com.java8.tms.program_content.service.ProgramContentService;
import com.java8.tms.technical_group.service.TechnicalGroupService;
import com.java8.tms.training_class.dto.Pagination;
import com.java8.tms.training_class.dto.*;
import com.java8.tms.training_class.service.TrainingClassService;
import com.java8.tms.training_class.utils.AppConstants;
import com.java8.tms.training_class.utils.DataUtil;
import com.java8.tms.training_program.service.TrainingProgramService;
import com.java8.tms.user.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ValidationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingClassServiceImpl implements TrainingClassService {
    private static final Logger logger = LogManager.getLogger(TrainingClassServiceImpl.class);
    private final AttendeeLevelService attendeeLevelService;
    private final ClassLocationService classLocationService;
    private final FormatTypeService formatTypeService;
    private final ClassStatusService classStatusService;
    private final TechnicalGroupService technicalGroupService;
    private final ProgramContentService programContentService;
    private final FSUService fsuService;
    private final TrainingProgramService trainingProgramService;
    private final UserService userService;
    private final SyllabusRepository syllabusRepository;
    
    final private static String NAME_STATUS_ENDED_IN_DATABASE = "Ended"; // use for Ended class
    final private static String NAME_STATUS_CLOSED_IN_DATABASE = "Closed"; // use for Closed class
    final private static String NAME_STATUS_OPENNING_IN_DATABASE = "Opening"; // use for Opening class
    final private static String NAME_STATUS_PLANNING_IN_DATABASE = "Planning"; // use for Planning class
    
    @Autowired
    private TrainingClassRepository trainingClassRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassLocationRepository classLocationRepository;
    @Autowired
    private AttendeeLevelRepository attendeeLevelRepository;
    @Autowired
    private FormatTypeRepository formatTypeRepository;
    @Autowired
    private ClassStatusRepository classStatusRepository;
    @Autowired
    private TechnicalGroupRepository technicalGroupRepository;
    @Autowired
    private ProgramContentRepository programContentRepository;
    @Autowired
    private FSURepository fsuRepository;
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;
    @Autowired
    private CustomExceptionHandler customExceptionHandler;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private EntityManager em;

    private final String Date_Parser = "E MMM dd HH:mm:ss Z yyyy";
    private final String Date_Formatter = "yyyy/MM/dd";
    public TrainingClassServiceImpl(AttendeeLevelService attendeeLevelService,
                                    ClassLocationService classLocationService,
                                    FormatTypeService formatTypeService,
                                    ClassStatusService classStatusService,
                                    TechnicalGroupService technicalGroupService,
                                    ProgramContentService programContentService,
                                    FSUService fsuService,
                                    TrainingProgramService trainingProgramService,
                                    UserService userService,
                                    TrainingClassRepository trainingClassRepository,
                                    SyllabusRepository syllabusRepository) {

        this.attendeeLevelService = attendeeLevelService;
        this.classLocationService = classLocationService;
        this.formatTypeService = formatTypeService;
        this.classStatusService = classStatusService;
        this.technicalGroupService = technicalGroupService;
        this.programContentService = programContentService;
        this.fsuService = fsuService;
        this.trainingProgramService = trainingProgramService;
        this.userService = userService;
        this.trainingClassRepository = trainingClassRepository;
        this.syllabusRepository = syllabusRepository;
    }

    public List<DataExcelForTrainingClass> readDataFromExcel(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!fileName.contains(".xlsx") || !fileName.contains(".xls"))
            throw new RuntimeException("file you have requested for reading must be in .xlsx or .xls");
        try {
            Workbook workbook = null;
            if (file instanceof MultipartFile) {
                byte[] b = file.getBytes();
                InputStream inputStream = new ByteArrayInputStream(b);
                if (fileName.contains(".xlsx"))
                    workbook = new XSSFWorkbook(inputStream);
                else if (fileName.contains(".xls"))
                    workbook = new HSSFWorkbook(inputStream);

            }

            Sheet sheet = workbook.getSheetAt(1);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            List<DataExcelForTrainingClass> list = new ArrayList<>();
            DataFormatter formatter = new DataFormatter();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter formatLocalDate = DateTimeFormatter.ofPattern("d/M/yyyy");
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("H:m");


            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                // DataExcelForTrainingClass trainingClassDTO = mapToDTO(newTraningClass);
                Row row = sheet.getRow(i);
                // dataExcelForTrainingClass=new DataExcelForTrainingClass();
                DataExcelForTrainingClass dataExcelForTrainingClass = new DataExcelForTrainingClass();

                if (row != null) {
                    UUID id = UUID.randomUUID();
                    dataExcelForTrainingClass.setId(id);
                    Cell c0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c1 = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c2 = row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c3 = row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c4 = row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c5 = row.getCell(5, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c6 = row.getCell(6, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c7 = row.getCell(7, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c8 = row.getCell(8, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c9 = row.getCell(9, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c10 = row.getCell(10, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c11 = row.getCell(11, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c12 = row.getCell(12, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c13 = row.getCell(13, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c14 = row.getCell(14, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c15 = row.getCell(15, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c16 = row.getCell(16, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c17 = row.getCell(17, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c18 = row.getCell(18, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c19 = row.getCell(19, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c20 = row.getCell(20, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c21 = row.getCell(21, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c22 = row.getCell(22, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c23 = row.getCell(23, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c24 = row.getCell(24, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c25 = row.getCell(25, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c26 = row.getCell(26, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c27 = row.getCell(27, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c28 = row.getCell(28, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c29 = row.getCell(29, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell c30 = row.getCell(30, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    if (c0 != null) {
                        String sttValue = formatter.formatCellValue(c0, evaluator).trim();
                        int stt = Integer.parseInt(sttValue);
                        dataExcelForTrainingClass.setStt(stt);

                    }
                    if (c1 != null) {
                        String site = formatter.formatCellValue(c1, evaluator).trim();
                        dataExcelForTrainingClass.setLocationId(site);

                    }
                    if (c2 != null) {
                        String noClassValue = formatter.formatCellValue(c2, evaluator).trim();
                        int noClass = Integer.parseInt(noClassValue);
                        dataExcelForTrainingClass.setNoClass(noClass);
                    }
                    if (c3 != null) {
                        String courseCode = formatter.formatCellValue(c3, evaluator).trim();
                        dataExcelForTrainingClass.setCourseCode(courseCode);

                    }
                    if (c4 != null) {
                        String status = formatter.formatCellValue(c4, evaluator).trim();
                        dataExcelForTrainingClass.setStatus(status);
                    }
                    if (c5 != null) {
                        String attendeeType = formatter.formatCellValue(c5, evaluator).trim();
                        dataExcelForTrainingClass.setAttendeeType(attendeeType);
                    }
                    if (c6 != null) {
                        String formatType = formatter.formatCellValue(c6, evaluator).trim();
                        dataExcelForTrainingClass.setFormatType(formatType);
                    }
                    if (c7 != null) {
                        String fsu = formatter.formatCellValue(c7, evaluator).trim();
                        dataExcelForTrainingClass.setFsu(fsu);
                    }
                    if (c8 != null) {
                        String universityCode = formatter.formatCellValue(c8, evaluator).trim();
                        dataExcelForTrainingClass.setUniversityCode(universityCode);
                    }
                    if (c9 != null) {
                        String technicalGroup = formatter.formatCellValue(c9, evaluator).trim();
                        dataExcelForTrainingClass.setTechnicalGroup(technicalGroup);
                    }
                    if (c10 != null) {
                        String trainingProgram = formatter.formatCellValue(c10, evaluator).trim();
                        dataExcelForTrainingClass.setTrainingProgram(trainingProgram);
                    }
                    if (c11 != null) {
                        String trainingProgramVersion = formatter.formatCellValue(c11, evaluator).trim();
                        dataExcelForTrainingClass.setTrainingProgramVersion(trainingProgramVersion);
                    }
                    if (c12 != null) {
                        String programContentId = formatter.formatCellValue(c12, evaluator).trim();
                        dataExcelForTrainingClass.setProgramContentId(programContentId);
                    }
                    if (c13 != null) {
                        String recer = formatter.formatCellValue(c13, evaluator).trim();
                        dataExcelForTrainingClass.setRecer(recer);
                    }
                    if (c14 != null) {
                        String traineeNoValue = formatter.formatCellValue(c14, evaluator).trim();
                        int traineeNo = Integer.parseInt(traineeNoValue);
                        dataExcelForTrainingClass.setTraineeNO(traineeNo);
                    }
                    if (c15 != null) {
                        String planStartDateString = formatter.formatCellValue(c15, evaluator).trim();
                        LocalDate planStartDate=LocalDate.parse(planStartDateString,formatLocalDate);
                        dataExcelForTrainingClass.setStartDate(planStartDate);

                    }
                    if (c15 != null & c16 != null & c17 != null) {
                        if (c16.getCellType() == CellType.FORMULA) {
                            String planStartDateString = formatter.formatCellValue(c15, evaluator).trim();
                            LocalDate planStartDate=LocalDate.parse(planStartDateString,formatLocalDate);
                            String durationValue = formatter.formatCellValue(c17, evaluator).trim();
                            int duration = Integer.parseInt(durationValue);
                            LocalDate planEndDate=planStartDate.plusMonths(duration).plusDays(-1);
                            dataExcelForTrainingClass.setEndDate(planEndDate);
                        } else {
                            String planEndDateValue = formatter.formatCellValue(c15, evaluator).trim();
                            LocalDate planEndDate=DateTimeUtils.convertStringToLocalDate(planEndDateValue);
                            dataExcelForTrainingClass.setEndDate(planEndDate);

                        }

                    }
                    if (c17 != null) {
                        String durationValue = formatter.formatCellValue(c17, evaluator).trim();
                        int duration = Integer.parseInt(durationValue);
                        dataExcelForTrainingClass.setDuration(duration);
                    }
                    if (c18 != null) {
                        String trainer = formatter.formatCellValue(c18, evaluator).trim();
                        Set<String> listTrainer = DataUtil.splitString(trainer);
                        System.out.println(listTrainer.size());
                        dataExcelForTrainingClass.setTrainer(listTrainer);
                    }
                    if (c19 != null) {
                        String mentor = formatter.formatCellValue(c19, evaluator).trim();
                        dataExcelForTrainingClass.setMentor(mentor);
                    }
                    if (c20 != null) {
                        String classAdmin = formatter.formatCellValue(c20, evaluator).trim();
                        Set<String> listClassAdmin = DataUtil.splitString(classAdmin);
                        dataExcelForTrainingClass.setClassAdmin(listClassAdmin);
                    }
                    if (c21 != null) {
                        String location = formatter.formatCellValue(c21, evaluator).trim();
                        dataExcelForTrainingClass.setLocationUnit(location);
                    }
                    if (c22 != null) {
                        String updatedDateValue = formatter.formatCellValue(c22, evaluator).trim();
//                        Date updatedDate = df.parse(updatedDateValue);
                        //dataExcelForTrainingClass.setUpdatedDate(updatedDate);
                    }
                    if (c23 != null) {
                        String updatedBy = formatter.formatCellValue(c23, evaluator).trim();
                        dataExcelForTrainingClass.setUpdatedBy(updatedBy);
                    }
                    if (c24 != null) {
                        String formatType_Abb = formatter.formatCellValue(c24, evaluator).trim();
                        dataExcelForTrainingClass.setFormatType_Abb(formatType_Abb);
                    }
                    if (c25 != null) {
                        String classNo_AbbValue = formatter.formatCellValue(c25, evaluator).trim();
                        int classNo_Abb = Integer.parseInt(classNo_AbbValue);
                        dataExcelForTrainingClass.setClassNo_Abb(classNo_Abb);
                    }
                    if (c26 != null) {
                        String universityCode_Abb = formatter.formatCellValue(c26, evaluator).trim();
                        dataExcelForTrainingClass.setUniversityCode_Abb(universityCode_Abb);
                    }
                    if (c27 != null) {
                        String startYearValue = formatter.formatCellValue(c27, evaluator).trim();
                        int startYear = Integer.parseInt(startYearValue);
                        dataExcelForTrainingClass.setStartYear(startYear);
                    }
                    if (c28 != null) {
                        String startTimeString = formatter.formatCellValue(c28, evaluator).trim();
                        try {
                            LocalTime startTime=LocalTime.parse(startTimeString,formatTime);

                            dataExcelForTrainingClass.setStartTime(startTime);
                        } catch (Exception ex) {

                        }
                    }
                    if (c29 != null) {
                        String endTimeString = formatter.formatCellValue(c29, evaluator).trim();
                        try {
                            LocalTime endTime=LocalTime.parse(endTimeString,formatTime);
                            dataExcelForTrainingClass.setEndTime(endTime);
                        } catch (Exception ex) {

                        }
                    }
                    if(c30 != null) {
                        String plannedAttendee = formatter.formatCellValue(c30, evaluator).trim();
                        int plannedAtt = Integer.parseInt(plannedAttendee);
                        dataExcelForTrainingClass.setPlannedAttendee(plannedAtt);
                    }
                    System.out.println("Tới dòng đây rồi");
                    saveTrainingClass(dataExcelForTrainingClass);
                    list.add(dataExcelForTrainingClass);
                }
            }
            return list;
        } catch (

                Exception ex) {

        }
        return null;
    }


    @Override
    public ClassResponse getAllClass(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (sortBy == null || sortDirection == null) {
            sortBy = AppConstants.DEFAULT_SORT_BY;
            sortDirection = AppConstants.DEFAULT_SORT_DIRECTION;
        }

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        ClassResponse classResponse = new ClassResponse();
        Page<TrainingClass> trainingClasses = trainingClassRepository.findAll(pageable);
        if (trainingClasses.hasContent()) {
            List<TrainingClass> listOfClass = trainingClasses.getContent();
            Pagination pagination = new Pagination();
            pagination.setPage(pageNumber);
            pagination.setLimit(trainingClasses.getSize());
            pagination.setTotalPage(trainingClasses.getTotalPages());
            List<ClassDTO> data = listOfClass.stream().map(trainingClass -> mapToClassDTO(trainingClass))
                    .collect(Collectors.toList());
            classResponse.setStatus("200");
            classResponse.setMessage("Ok");
            classResponse.setPagination(pagination);
            classResponse.setData(data);

        } else {
            classResponse.setStatus("200");
            classResponse.setMessage("Ok");
        }
        return classResponse;
    }

    @Override
    public ClassResponse getClass(int pageNumber, int pageSize, String sortBy, String sortType, List<String> searchValue,
                                  List<String> location, Date from, Date to, List<String> classTime,
                                  List<String> status, List<String> attendeeType, String fsu, String trainer) {
        if(sortBy == null || sortType == null) {
            sortBy = AppConstants.DEFAULT_SORT_BY;
            sortType = AppConstants.DEFAULT_SORT_DIRECTION;
        }
        Sort sort = sortType.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        ClassResponse classResponse = new ClassResponse();
        Page<TrainingClass> trainingClasses = getClassResponseContent(pageable, searchValue, location,
                from, to, classTime, status, attendeeType, fsu, trainer);
        if (trainingClasses.hasContent()) {
            List<TrainingClass> listOfClass = trainingClasses.getContent();
            Pagination pagination = new Pagination();
            pagination.setPage(pageNumber);
            pagination.setLimit(trainingClasses.getSize());
            pagination.setTotalPage(trainingClasses.getTotalPages());
            List<ClassDTO> data = listOfClass.stream().map(trainingClass -> mapToClassDTO(trainingClass))
                    .collect(Collectors.toList());
            classResponse.setStatus("200");
            classResponse.setMessage("Ok");
            classResponse.setPagination(pagination);
            classResponse.setData(data);

        } else {
            classResponse.setStatus("200");
            classResponse.setMessage("Ok");
        }
        return classResponse;
    }

    private Page<TrainingClass> getClassResponseContent(Pageable pageable ,List<String> searchValue,
                                                        List<String> location, Date from, Date to, List<String> classTime, List<String> status,
                                                        List<String> attendeeType, String fsu, String trainer) {

        String query = "select * from training_class a";
        String addQuery = "";

//        Search tất cả field
        if (searchValue != null && searchValue.size() != 0) {
            addQuery = addSearchValue(searchValue);
        }

//        Filter location
        if (location != null && location.size() != 0) {
            addQuery = addListOfValueToQuery(addQuery, location, "(Select name from class_location where id = a.class_location_id)", " LIKE ", "");
        }


//        Filter time frame
        if (from != null) {
            try {
                String dateStr = "" + from;
                DateFormat parser = new SimpleDateFormat(Date_Parser);
                DateFormat formatter = new SimpleDateFormat(Date_Formatter);
                String value = formatter.format(parser.parse(dateStr));
                addQuery = addSingleValueToQuery(addQuery, value, "start_date", " >= ", "");
            } catch (Exception e) {}
        }

        if (to != null) {
            try {
                String dateStr = "" + to;
                DateFormat parser = new SimpleDateFormat(Date_Parser);
                DateFormat formatter = new SimpleDateFormat(Date_Formatter);
                String value = formatter.format(parser.parse(dateStr));
                addQuery = addSingleValueToQuery(addQuery, value, "end_date", " <= ", "");
            } catch (Exception e) {}
        }

//        Filter class time (chưa check)
        if (classTime != null && classTime.size() != 0) {
            addQuery = addClassTimeValue(addQuery, classTime);
        }

//        Filter status
        if (status != null  && status.size() != 0) {
            addQuery = addListOfValueToQuery(addQuery, status, "(Select name from class_status where id = a.class_status_id)", " LIKE ", "");
        }

//        Filter attendee type
        if (attendeeType != null && attendeeType.size() != 0) {
            addQuery = addListOfValueToQuery(addQuery, attendeeType, "(Select name from attendee_level where id = a.attendee_level_id)", " LIKE ", "");
        }

//         Filter FSU
        if (fsu != null && fsu.length() != 0) {
            addQuery = addSingleValueToQuery(addQuery, fsu, "(Select name from fsu where id = a.fsu_id)", " LIKE ", "");
        }


//         Filter trainer (Trả về xâu là UUID)
        if (trainer != null && trainer.length() != 0) {
            addQuery = addSingleValueToQuery(addQuery, trainer, "id in (select class_id from class_trainers where trainer_id"," = ", ")");
        }

        if (!addQuery.equals("")) query+=(" where " + addQuery);
        System.out.println(query);
        Query nativeQuery = em.createNativeQuery(query, TrainingClass.class);
        List<TrainingClass> list = nativeQuery.getResultList();
        List<TrainingClass> returnList = list;

        if (pageable.isPaged()) {
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            int startItem = currentPage * pageSize;
            if (list.size() < startItem) {
                returnList = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, list.size());
                returnList = list.subList(startItem, toIndex);
            }
        }

        Page<TrainingClass> result = new PageImpl<>(returnList, pageable, list.size());

        return result;
    }

    public String addSearchValue (List<String> keyword) {
        String query = "";
        for (String key : keyword) {
            String value = editSearchValue(key);
            query += "((Select name from training_program where id = a.training_program_id) LIKE '" + value + "' OR ";
            query += "(Select name from attendee_level where id = a.attendee_level_id) LIKE '" + value + "' OR ";
            query += "(Select name from class_status where id = a.class_status_id) LIKE '" + value + "' OR ";
            query += "(Select name from class_location where id = a.class_location_id) LIKE '" + value + "' OR ";
            query += "duration LIKE '" + value + "' OR ";
            query += "course_code LIKE '" + value + "' OR ";
            query += "(Select name from fsu where id = a.fsu_id) LIKE '" + value + "') AND ";
        }
        query = query.substring(0, query.length() - 5);
        return query;
    }

    public String editSearchValue (String key) {
        String value = "%";
        for (int i = 0; i<key.length(); i++) {
            value += key.charAt(i) + "%";
        }
        return value;
    }

    public String addClassTimeValue (String query, List<String> value) {
        query = checkFirstQuery(query);
        query += "(";
        boolean notClassTime = true;
        boolean notFormatType = true;
        boolean addConfirm = false;
        String fromClassTime = "";
        String toClassTime = "";
        String formatType = "";
        for (String key : value) {
            try {
                notClassTime = true;
                notFormatType = true;
                switch (key) {
                    case "Morning":
                        fromClassTime = "08:00:00";
                        toClassTime = "12:00:00";
                        notClassTime = false;
                        break;
                    case "Noon":
                        fromClassTime ="13:00:00";
                        toClassTime = "17:00:00";
                        notClassTime = false;
                        break;
                    case "Night":
                        fromClassTime = "18:00:00";
                        toClassTime = "22:00:00";
                        notClassTime = false;
                        break;
                    case "Online":
                        formatType = "Online";
                        notFormatType = false;
                        break;
                }
                if (notClassTime == false) {
                    query += "(start_time >= '" + fromClassTime + "' AND end_time <= '" + toClassTime + "') OR ";
                    addConfirm = true;
                }
            } catch (Exception ex) {
            }
        }

        if (addConfirm == true) {
            query = query.substring(0, query.length() - 4);
        }
        else if (query.equals("(")) {
            query = "";
        }
        else {
            query = query.substring(0, query.length() - 6);
        }

        if (!query.equals("")) {
            query += ")";
        }

        if (notFormatType == false) {
            if (!query.equals("")) {
                query += " AND ";
            }
            query += "(select name from format_type where id = a.format_type_id) LIKE '" + formatType + "'";
        }

        return query;
    }

    public String addSingleValueToQuery(String query, String value, String type, String compareFront, String compareEnd) {
        query = checkFirstQuery(query);
        query += type + compareFront + "'" + value + "'" + compareEnd;
        return query;
    }

    public String addListOfValueToQuery(String query, List<String> value, String type, String compareFront, String compareEnd) {
        query = checkFirstQuery(query);
        query += "(";
        for (String key : value)
            query += type + compareFront + "'" + key + "'" + compareEnd + " OR ";
        query = query.substring(0, query.length() - 4);
        query += ")";
        return query;
    }

    public String checkFirstQuery(String query) {
        if (query.length()==0)
            return query;
        else
            return (query + " AND ");
    }

    @Override
    public List<String> getSuggestion(String keyword) {
        List<String> list = new ArrayList<>();
        List<String> listQuery = new ArrayList<>();
        String value = editSearchValue(keyword);

        listQuery.add("select name from training_program where name LIKE '" + value + "' group by name");
        listQuery.add("select name from attendee_level where name LIKE '" + value + "' group by name");
        listQuery.add("select name from class_status where name LIKE '" + value + "' group by name");
        listQuery.add("select name from class_location where name LIKE '" + value + "' group by name");
        listQuery.add("select duration from training_class a where duration LIKE '" + value + "' group by duration");
        listQuery.add("select course_code from training_class a where course_code LIKE '" + value + "' group by course_code");
        listQuery.add("select name from fsu where name LIKE '" + value + "' group by name");

        for (String key : listQuery) {
            Query nativeQuery = em.createNativeQuery(key);
            List<String> contain = nativeQuery.getResultList();
            for (String getValue : contain)
                if (!list.contains(getValue) && list.size()<30) list.add(getValue);
        }

        return list;
    }

    @Override
    public ClassData getAllFieldData() {
        ClassData list = new ClassData();
        List<String> fsuInfo = new ArrayList<>();
        List<String> locationInfo = new ArrayList<>();
        List<Info> trainerInfo =  new ArrayList<>();
        String query = "select * from user where id in (select trainer_id from class_trainers)";
        Query nativeQuery = em.createNativeQuery(query, User.class);

        List<FSU> fsu = fsuService.findAll();
        System.out.println(fsu);
        List<ClassLocation> classLocation = classLocationService.findAll();
        System.out.println(classLocation);
        List<User> trainer = nativeQuery.getResultList();
        System.out.println(trainer);

        for (FSU key : fsu) fsuInfo.add(key.getName());
        list.setFsu(fsuInfo);

        for (ClassLocation key : classLocation) locationInfo.add(key.getName());
        list.setLocation(locationInfo);

        for (User key : trainer) {
            UUID id = key.getId();
            String name = key.getFullname();
            Info newInfo = new Info();
            newInfo.setId(id);
            newInfo.setName(name);
            trainerInfo.add(newInfo);
        }
        list.setTrainers(trainerInfo);

        return list;
    }

    @Override
    public ClassDTO getClassById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
        return mapToClassDTO(trainingClass);
    }

    // @Override
    // public ClassDTO getClassById(UUID id) {
    // TrainingClass trainingClass =
    // trainingClassRepository.findById(id).orElseThrow(() -> new
    // ResourceNotFoundException("Class", "id", id));
    // return mapToClassDTO(trainingClass);
    // }

    private ClassDTO mapToClassDTO(TrainingClass trainingClass) {
        ClassDTO classDTO = new ClassDTO();
        // TrainingProgram trainingProgram = new TrainingProgram();
        classDTO.setId(trainingClass.getId());
        classDTO.setCourseCode(trainingClass.getCourseCode());
        classDTO.setTrainingProgram(trainingClass.getTrainingProgram().getName());
        classDTO.setDuration(trainingClass.getDuration());
        classDTO.setAttendee(trainingClass.getAttendeeLevel().getName());
        classDTO.setStatus(trainingClass.getClassStatus().getName());
        classDTO.setStartDate(trainingClass.getStartDate());
        classDTO.setEndDate(trainingClass.getEndDate());
        classDTO.setLocation(trainingClass.getClassLocation().getName());
        classDTO.setFsu(trainingClass.getFsu().getName());
        return classDTO;
    }

    private ClassFilterDTO mapToClassFilterDTO(TrainingClass trainingClass) {
        ClassFilterDTO classFilterDTO = new ClassFilterDTO();
        // TrainingProgram trainingProgram = new TrainingProgram();
        classFilterDTO.setId(trainingClass.getId());
        classFilterDTO.setCourseCode(trainingClass.getCourseCode());
        classFilterDTO.setNameTrainingProgram(trainingClass.getTrainingProgram().getName());
        classFilterDTO.setDuration(trainingClass.getDuration());
        classFilterDTO.setNameAttendee(trainingClass.getAttendeeLevel().getName());
        classFilterDTO.setNameStatus(trainingClass.getClassStatus().getName());
        classFilterDTO.setNameLocation(trainingClass.getClassLocation().getName());
        classFilterDTO.setNameFsu(trainingClass.getFsu().getName());
        classFilterDTO.setTrainer(trainingClass.getAccount_trainers());
        //classFilterDTO.setStartDate(trainingClass.getStartDate());
        //classFilterDTO.setEndDate(trainingClass.getEndDate());
        //classFilterDTO.setStartTime(new Time(trainingClass.getStartTime().getTime()));
        //classFilterDTO.setEndTime(new Time(trainingClass.getEndTime().getTime()));
        classFilterDTO.setNameFormatType(trainingClass.getFormatType().getName());
        return classFilterDTO;
        // ClassDTO classFilterDTO = mapper.map(trainingClass, ClassDTO.class);
        // return classFilterDTO;
    }

//    @Override
//    public TrainingClassDTO createTrainingClass(TrainingClassDTO trainingClassDTO) {
//        TrainingClass trainingClass = mapToEntity(trainingClassDTO);
//        TrainingClass newTrainingClass = trainingClassRepository.save(trainingClass);
//
//        TrainingClassDTO trainingClassResponse = mapToDTO(newTrainingClass);
//        System.out.println("tới đây rồi nè");
//        return trainingClassResponse;
//    }

    // @Override
    // public TrainingClassDTO createTrainingClass(TrainingClassDTO
    // trainingClassDTO) {
    // TrainingClass trainingClass = mapToEntity(trainingClassDTO);
    // TrainingClass newTrainingClass = trainingClassRepository.save(trainingClass);
    //
    // TrainingClassDTO trainingClassResponse = mapToDTO(newTrainingClass);
    // System.out.println("tới đây rồi nè");
    // return trainingClassResponse;
    // }

    // convert Entity into DTO
    private TrainingClassDTO mapToDTO(TrainingClass trainingClass) {
        TrainingClassDTO dto = mapper.map(trainingClass, TrainingClassDTO.class);
        return dto;
    }

    // convert DTO into Entity
    private TrainingClass mapToEntity(TrainingClassDTO dto) {
        TrainingClass entity = mapper.map(dto, TrainingClass.class);
        return entity;
    }

    public void saveTrainingClass(DataExcelForTrainingClass dataExcelForTrainingClass) {
        logger.info("saveTrainingClass");

        TrainingProgram trainingProgram = findTrainingProgramByNameAndVersion(dataExcelForTrainingClass.getTrainingProgram(),dataExcelForTrainingClass.getTrainingProgramVersion());
        TrainingClass trainingProgramInTrainingClass = findClassByTrainingProgramId(trainingProgram);
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (trainingClassRepository.findByCourseCode(dataExcelForTrainingClass.getCourseCode()) == null) {
            if (trainingProgram != null) {
                if (trainingProgramInTrainingClass == null) {
                    System.out.println("Bat dau insert");
                    TrainingClass trainingClass = new TrainingClass();
                    trainingClass.setCreatedBy(findUserByEmail(userPrinciple.getEmail()));
                    trainingClass.setId(dataExcelForTrainingClass.getId());
                    trainingClass.setCourseCode(dataExcelForTrainingClass.getCourseCode());
                    trainingClass.setStartTime(dataExcelForTrainingClass.getStartTime());
                    trainingClass.setEndTime(dataExcelForTrainingClass.getEndTime());
                    trainingClass.setStartDate(dataExcelForTrainingClass.getStartDate());
                    trainingClass.setEndDate(dataExcelForTrainingClass.getEndDate());
                    trainingClass.setDuration(dataExcelForTrainingClass.getDuration());
                    trainingClass.setUpdatedBy(findUserByEmail(dataExcelForTrainingClass.getUpdatedBy()));
                    trainingClass.setUpdatedDate(dataExcelForTrainingClass.getUpdatedDate());
                    trainingClass.setUniversityCode(dataExcelForTrainingClass.getUniversityCode());
                    trainingClass.setClassLocation(findClassLocationByName(dataExcelForTrainingClass.getLocationId()));
                    trainingClass.setAttendeeLevel(findAttendeeLevelByName(dataExcelForTrainingClass.getAttendeeType()));
                    trainingClass.setFormatType(findFormatTypeByName(dataExcelForTrainingClass.getFormatType()));
                    trainingClass.setClassStatus(findClassStatusByName(dataExcelForTrainingClass.getStatus().trim()));
                    trainingClass.setTechnicalGroup(findTechnicalGroupByName(dataExcelForTrainingClass.getTechnicalGroup()));
                    trainingClass.setProgramContent(findProgramContentByName(dataExcelForTrainingClass.getProgramContentId()));
                    trainingClass.setFsu(findFsuByName(dataExcelForTrainingClass.getFsu()));
                    trainingClass.setTrainingProgram(trainingProgram);
                    trainingClass.setPlannedAttendee(dataExcelForTrainingClass.getPlannedAttendee());
                    Set<String> listTrainer = dataExcelForTrainingClass.getTrainer();
                    System.out.println(listTrainer.size());
                    Set<User> trainers = new HashSet<>();
                    for (String value : listTrainer) {
                        User trainer = userRepository.findUserByEmail(value);
                        trainers.add(trainer);
                        trainingClass.setAccount_trainers(trainers);
                    }
                    Set<String> listAdmin = dataExcelForTrainingClass.getClassAdmin();
                    Set<User> admins = new HashSet<>();
                    for (String value : listAdmin) {
                        User admin = userRepository.findUserByEmail(value);
                        admins.add(admin);
                        trainingClass.setAccount_admins(admins);
                    }
                    trainingClassRepository.save(trainingClass);
                } else {
                    dataExcelForTrainingClass.setMessageError("TrainingProgram is duplicated with another class");

                }
            } else {
                dataExcelForTrainingClass.setMessageError("TrainingProgram does not exist");
            }
        } else {
            dataExcelForTrainingClass.setMessageError("Already has this class with same courseCode");

        }


    }

    private TrainingClass findClassByTrainingProgramId(TrainingProgram trainingProgramId) {
        return trainingClassRepository.findClassByTrainingProgramId(trainingProgramId);

    }

    private TrainingProgram findTrainingProgramByNameAndVersion(String name, String version) {
        return trainingProgramRepository.findByName(name,version);
    }

    private FSU findFsuByName(String name) {
        return fsuRepository.findByName(name);

    }

    private ProgramContent findProgramContentByName(String name) {
        return programContentRepository.findByName(name);

    }

    private TechnicalGroup findTechnicalGroupByName(String name) {
        return technicalGroupRepository.findByName(name);
    }

    private User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    private ClassLocation findClassLocationByName(String classLocationName) {
        return classLocationRepository.findByName(classLocationName);
    }

    private AttendeeLevel findAttendeeLevelByName(String name) {
        return attendeeLevelRepository.findByName(name);
    }

    private FormatType findFormatTypeByName(String name) {
        return formatTypeRepository.findByName(name);
    }

    private ClassStatus findClassStatusByName(String name) {
        return classStatusRepository.findByName(name);
    }

    @Override
    public TrainingClass update(UUID id, UpdateClassForm updateClassForm) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElseThrow(() -> new ValidationException("Training class is not existed"));
        TrainingProgram trainingProgram = trainingProgramService.findById(updateClassForm.getTrainingProgramId())
                .orElseThrow(() -> new ValidationException("Training program is not existed"));
        int count = 0;
        trainingClass.setTrainingProgram(trainingProgram);
        trainingClass.setName(updateClassForm.getName());

        try {
            trainingClass.setStartTime(DateTimeUtils.convertStringToLocalTime(updateClassForm.getStartTime()));
            trainingClass.setEndTime(DateTimeUtils.convertStringToLocalTime(updateClassForm.getEndTime()));
            trainingClass.setStartDate(DateTimeUtils.convertStringToLocalDate(updateClassForm.getStartDate()));
            trainingClass.setDuration(updateClassForm.getDuration());
            LocalDate endDate = trainingClass.getStartDate().plusMonths(trainingClass.getDuration()).plusDays(-1);
            trainingClass.setEndDate(endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        trainingClass.setUpdatedBy(findUserByEmail(userPrinciple.getEmail()));
        List<User> trainers = userService.findAllById(updateClassForm.getAccountTrainerIds());
        Set<User> tranierSet = new HashSet<>(trainers);
        trainingClass.setAccount_trainers(tranierSet);
        List<User> admins = userService.findAllById(updateClassForm.getAccountAdminIds());
        Set<User> adminSet = new HashSet<>(admins);
        trainingClass.setAccount_admins(adminSet);
        List<User> trainees = new ArrayList<>();

        for(UUID uuid : updateClassForm.getAccountTraineeIds()){
            trainees.add(userService.findById(uuid));
        }
        trainingClass.setAccount_trainee(trainees);
        trainingClass.setPlannedAttendee(updateClassForm.getPlannedAttendee());
        trainingClass.setAcceptedAttendee(updateClassForm.getAcceptedAttendee());
        trainingClass.setActualAttendee(updateClassForm.getActualAttendee());
        trainingClass.setAttendeeLevel(attendeeLevelService.findById(updateClassForm.getAttendeeLevelId()));

        trainingClass.setClassStatus(classStatusService.findById(updateClassForm.getClassStatusId()));
        if(trainingClass.getClassStatus() == classStatusService.findByName("Opening")) {
            trainingClass.setApprovedBy(findUserByEmail(userPrinciple.getEmail()));
            trainingClass.setApprovedDate(LocalDateTime.now());
        }
        trainingClass.setFsu(fsuService.findById(updateClassForm.getFsuId()));
        trainingClass.setClassLocation(classLocationService.findById(updateClassForm.getClassLocationId()));

        String newCode = autoFormatClassCode(trainingClass,count);
        newCode = checkDuplicateClassCode(trainingClass,newCode);
        if(newCode.equals("haved in db")) {
            trainingClassRepository.save(trainingClass);
        }
        else {
            trainingClass.setCourseCode(newCode);
            trainingClassRepository.save(trainingClass);
        }

        return trainingClass;
    }
    public String checkDuplicateClassCode(TrainingClass trainingClass, String newCode) {
        int count = 0;
        boolean loop = false;
        String oldCode = trainingClass.getCourseCode();
        do {
            try {
                if(findAllClassCode().contains(oldCode)) {
                    newCode = "haved in db";
                    loop = false;
                }

                if(findAllClassCode().contains(newCode)) {
                    newCode = autoFormatClassCode(trainingClass,count++);
                    System.out.println(newCode);
                }
                else {
                    loop = false;
                }

            }catch (Exception e) {
                loop = true;
            }

        }while(loop);

        return newCode;

    }

    @Override
    public TrainingClass createDuplicateClass(UUID id) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int count = 0;
        boolean loop = false;
        TrainingClass trainingClass = trainingClassRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("Training class is not existed"));
        TrainingClass trainingClassDuplicate = new TrainingClass();
        do {
            try {
                trainingClassDuplicate.setStartTime(trainingClass.getStartTime());
                trainingClassDuplicate.setEndTime(trainingClass.getEndTime());
                trainingClassDuplicate.setStartDate(trainingClass.getStartDate());
                trainingClassDuplicate.setEndDate(trainingClass.getEndDate());
                trainingClassDuplicate.setDuration(trainingClass.getDuration());
                trainingClassDuplicate.setCreatedBy(findUserByEmail(userPrinciple.getEmail()));
                trainingClassDuplicate.setUniversityCode(trainingClass.getUniversityCode());
                trainingClassDuplicate.setPlannedAttendee(trainingClass.getPlannedAttendee());
                trainingClassDuplicate.setAcceptedAttendee(trainingClass.getAcceptedAttendee());
                trainingClassDuplicate.setActualAttendee(trainingClass.getActualAttendee());
                trainingClassDuplicate.setClassLocation(trainingClass.getClassLocation());
                trainingClassDuplicate.setAttendeeLevel(trainingClass.getAttendeeLevel());
                trainingClassDuplicate.setFormatType(trainingClass.getFormatType());
                trainingClassDuplicate.setClassStatus(classStatusService.findByName("Planning"));
                trainingClassDuplicate.setTechnicalGroup(trainingClass.getTechnicalGroup());
                trainingClassDuplicate.setProgramContent(trainingClass.getProgramContent());
                trainingClassDuplicate.setFsu(trainingClass.getFsu());
                trainingClassDuplicate.setTrainingProgram(trainingClass.getTrainingProgram());
                trainingClassDuplicate.setCourseCode(autoFormatClassCode(trainingClassDuplicate, count++));
                trainingClassDuplicate.setName(trainingClass.getTrainingProgram().getName());
                trainingClassRepository.save(trainingClassDuplicate);
                loop = false;
            }catch (Exception e) {
                loop = true;
            }
        }while(loop);
        return trainingClassDuplicate;

    }

    public TrainingClassUpdateDTO mapTrainingClassToDto (TrainingClass trainingClass) {
        if(trainingClass == null) {
            return null;
        }
        TrainingClassUpdateDTO trainingClassUpdateDTO = mapper.map(trainingClass, TrainingClassUpdateDTO.class);

        trainingClassUpdateDTO.setCreatedBy(trainingClass.getCreatedBy().getFullname());
//        if(trainingClass.getUpdatedBy() == null) {
//            trainingClassUpdateDTO.setUpdatedBy(null);
//        }else {
//            trainingClassUpdateDTO.setUpdatedBy(trainingClass.getUpdatedBy().getFullname());
//        }
//        if(trainingClass.getReviewedBy() == null) {
//            trainingClassUpdateDTO.setReviewedBy(null);
//        }else {
//            trainingClassUpdateDTO.setReviewedBy(trainingClass.getReviewedBy().getFullname());
//        }
        if(trainingClass.getApprovedBy() == null) {
            trainingClassUpdateDTO.setApprovedBy(null);
        }else {
            trainingClassUpdateDTO.setApprovedBy(trainingClass.getApprovedBy().getFullname());
        }

        trainingClassUpdateDTO.setClassLocation(trainingClass.getClassLocation().getName());
        trainingClassUpdateDTO.setAttendeeLevel(trainingClass.getAttendeeLevel().getName());
        trainingClassUpdateDTO.setFormatType(trainingClass.getFormatType().getName());
        trainingClassUpdateDTO.setClassStatus(trainingClass.getClassStatus().getName());
        trainingClassUpdateDTO.setTechnicalGroup(trainingClass.getTechnicalGroup().getName());
        trainingClassUpdateDTO.setProgramContent(trainingClass.getProgramContent().getName());
        trainingClassUpdateDTO.setFsu(trainingClass.getFsu().getName());
        trainingClassUpdateDTO.setTrainingProgram(trainingClass.getTrainingProgram());
        try {
            trainingClassUpdateDTO.setUpdatedBy(trainingClass.getUpdatedBy().getFullname());
            trainingClassUpdateDTO.setReviewedBy(trainingClass.getReviewedBy().getFullname());
            //trainingClassUpdateDTO.setApprovedBy(trainingClass.getApprovedBy().getFullname());
            trainingClassUpdateDTO.setAccount_trainers(trainingClass.getAccount_trainers());
            trainingClassUpdateDTO.setAccount_admins(trainingClass.getAccount_admins());
            trainingClassUpdateDTO.setAccount_trainee(trainingClass.getAccount_trainee());
        }catch (Exception e) {

        }
        return trainingClassUpdateDTO;
    }

    @Override
    public List<String> findAllClassCode() {
        return trainingClassRepository.findAllClassCode();
    }

    /**
     *
     * @param trainingClass
     * @param count
     * @return Class code Example: HCM22_CPL.O_JAVA_01
     * @note
     * first: HCM22
     * second: CPL.0
     * third: JAVA
     * last: 01
     * @author Duy Le
     */
    public String autoFormatClassCode(TrainingClass trainingClass, int count) {
        String first = trainingClass.getClassLocation().getName() +
                trainingClass.getStartDate().toString().substring(2, 4);

        String second = trainingClass.getAttendeeLevel().getName();
        if (trainingClass.getFormatType().getName().equals("Online")) {
            second = second + ".O";
        }

        String third = trainingClass.getProgramContent().getName();

        String last;

        if(count != 0) {

            if(count < 10) {
                last = String.format("%02d", count++);
            }
            else {
                last = String.valueOf(count++);
            }

        }else {
            return first + "_" + second + "_" + third;
        }

        return first + "_" + second + "_" + third + "_" + last;
    }


    public ResponseEntity<ResponseObject> viewDetailClass(UUID id) {
        TrainingClassDTO trainingClassDTO = new TrainingClassDTO();


        System.out.println("here1");
        try {
            TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
            if (trainingClass == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
            }
//            // ==> giữ lại cái này nha
//            trainingClass.setCreatedBy(null);
//            trainingClass.setUpdatedBy(null);
//            trainingClass.setReviewedBy(null);
//            trainingClass.setApprovedBy(null);
//            trainingClass.setClassLocation(null);
//            trainingClass.setAttendeeLevel(null);
//            trainingClass.setFormatType(null);
//            trainingClass.setClassStatus(null);
//            trainingClass.setTechnicalGroup(null);
//            trainingClass.setProgramContent(null);
//            trainingClass.setFsu(null);
//            trainingClass.setTrainingProgram(null);
//            trainingClass.setAccount_trainers(null);
//            trainingClass.setAccount_admins(null);
//            trainingClass.setClassTrainee(null);
//            trainingClass.setClassCalendars(null);

            System.out.println("here3");
            trainingClassDTO.setTrainingClass(trainingClass);
            trainingClassDTO.setTrainingClassCreatedBy((String) getClassCreatedByById(id).getBody().getData());
            try{
                trainingClassDTO.setTrainingClassUpdatedBy((String) getClassUpdatedByById(id).getBody().getData());
                trainingClassDTO.setTrainingClassReviewedBy((String) getClassReviewedByById(id).getBody().getData());
                trainingClassDTO.setTrainingClassApprovedBy((String) getClassApprovedByById(id).getBody().getData());
            } catch (Exception ex) {
                //no user update,no user review,no user approve
            }

            trainingClassDTO.setTrainingProgram((TrainingProgram) getClassTrainingProgram(id).getBody().getData());

            trainingClassDTO.setListAdmin((Set<User>) getClassAdmin(id).getBody().getData());
            trainingClassDTO.setListTrainer((Set<User>) getClassTrainer(id).getBody().getData());
            trainingClassDTO.setListAttendee((List<User>) getClassTrainee(id).getBody().getData());

            trainingClassDTO.setLocationName((String) getClassLocation(id).getBody().getData());

            trainingClassDTO
                    .setListProgramSyllabus((List<ProgramSyllabus>) getClassProgramSyllabus(id).getBody().getData());

            trainingClassDTO.setNameAttendeeLevel((String) getClassAttendeeLevel(id).getBody().getData());
            trainingClassDTO.setNameFormatType((String) getClassFormatType(id).getBody().getData());
            trainingClassDTO.setNameClassStatus((String) getClassStatus(id).getBody().getData());

            trainingClassDTO.setNameTechnicalGroup((String) getTechnicalGroup(id).getBody().getData());
            trainingClassDTO.setNameClassFsu((String) getClassFsu(id).getBody().getData());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "successful", null, trainingClassDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", "Fail, Something wrong. Please try again !", null, null));
        }
    }

//    @Override
//    public ResponseEntity<ResponseObject> deleteClass(String id, String nameStatus) {
//        // check format UUID
//        try {
//            UUID.fromString(id);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new ResponseObject("Not Found", "Unsuccessful, Not correct format ID: " + id, null, null));
//        }
//
//        TrainingClass trainingClass = trainingClassRepository.findById(UUID.fromString(id)).orElse(null);
//        if (trainingClass == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
//        }
//
//        ClassStatus classStatus = classStatusRepository.findByName(nameStatus);
//        classStatus.setTrainingClasses(null);
//        trainingClass.setClassStatus(classStatus);
//        trainingClassRepository.save(trainingClass);
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject("OK", "Successful, Delete class", null, null));
//    }

    // @Override
//    public ResponseEntity<ResponseObject> getClassById(UUID id) {
//        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
//        if (trainingClass == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject("OK", "successful", null, trainingClass));
//    }
    /*
     * @Override
     * public ResponseEntity<ResponseObject> getClassCalendar(UUID id){
     * TrainingClass trainingClass =
     * trainingClassRepository.findById(id).orElse(null);
     * if(trainingClass == null){
     * return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
     * new ResponseObject("Not Found", "Unsuccessful, Not found class by id"
     * , null, null));
     * }
     *
     * List<ClassCalendar> listclasscalendar = classCalendarRepository.findAll();
     * List<ClassCalendar> listclasscalendarbyclassid = new ArrayList<>();
     * for(ClassCalendar listclasscalendar_ : listclasscalendar){
     * if(listclasscalendar_.getTrainingClass().getId() == id){
     * listclasscalendarbyclassid.add(listclasscalendar_);
     * }
     * }
     * return ResponseEntity.status(HttpStatus.OK).body(
     * new ResponseObject("OK", "successful", null,
     * listclasscalendarbyclassid));
     * }
     */

    // Those functions are create for optional only (get a specific data instead of
    // get all)
    @Override
    public ResponseEntity<ResponseObject> getClassNameById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getName()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassCodeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getCourseCode()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassStartTimeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getStartTime()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassEndTimeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getEndTime()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassStartDateById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getStartDate()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassEndDateById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getEndDate()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassDurationById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getDuration()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassCreatedByById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getCreatedBy().getEmail()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassCreatedDateById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getCreatedDate()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassUpdatedByById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getUpdatedBy().getEmail()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassUpdatedDateById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getUpdatedDate()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassReviewedByById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getReviewedBy().getEmail()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassReviewedDateById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getReviewedDate()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassApprovedByById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getApprovedBy().getEmail()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassApprovedDateById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getApprovedDate()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassUniversityCodeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getUniversityCode()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassPlannedAttendeeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getPlannedAttendee()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassAcceptedAttendeeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getAcceptedAttendee()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassActualAttendeeById(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "successful", null, trainingClass.getActualAttendee()));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResponseEntity<ResponseObject> getClassTrainingProgram(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found training program by class id", null,
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getTrainingProgram()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassTrainer(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found trainers by class id", null, null));
        }
        Set<User> listAdmin = trainingClass.getAccount_trainers();
        //set null các field bị đệ quy thay cho @JsonIgnore
        for (User userr: listAdmin) {
            userr.setFsu(null);
            userr.setRole(null);
            userr.setCreatedClasses(null);
            userr.setUpdatedClasses(null);
            userr.setReviewedClasses(null);
            userr.setApprovedClasses(null);
            userr.setOtp(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, listAdmin));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassAdmin(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found admins by class id", null, null));
        }
        Set<User> listAdmin = trainingClass.getAccount_admins();
        //set null các field bị đệ quy thay cho @JsonIgnore
        for (User userr: listAdmin) {
            userr.setFsu(null);
            userr.setRole(null);
            userr.setCreatedClasses(null);
            userr.setUpdatedClasses(null);
            userr.setReviewedClasses(null);
            userr.setApprovedClasses(null);
            userr.setOtp(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, listAdmin));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassProgramSyllabus(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found program syllabus by class id", null,
                            null));
        }
        List<ProgramSyllabus> ps = trainingClass.getTrainingProgram().getProgramSyllabusAssociation();
        List<Syllabus> s = new ArrayList<>();
        for (ProgramSyllabus ps_ : ps) {
            s.add(ps_.getSyllabus());
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, s));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassLocation(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class location by class id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getClassLocation().getName()));
    }

    @Override
    public ResponseEntity<ResponseObject> getClassTrainee(UUID id) {
        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found admins by class id", null, null));
        }
        List<User> listTrainee = trainingClass.getAccount_trainee();
        //set null các field bị đệ quy thay cho @JsonIgnore
        for (User users: listTrainee) {
            users.setFsu(null);
            users.setRole(null);
            users.setCreatedClasses(null);
            users.setUpdatedClasses(null);
            users.setReviewedClasses(null);
            users.setApprovedClasses(null);
            users.setOtp(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, listTrainee));

    }

//    @Override
//    public ResponseEntity<ResponseObject> getClassAttendeeList(UUID id) {
//        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
//        if (trainingClass == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObject("Not Found", "Unsuccessful, Not found attendee list by class id", null, null));
//        }
//        List<ClassTrainee> ctl1 = trainingClass.getClassTrainee();
//        List<User> ctl2 = new ArrayList<>();
//        for (ClassTrainee ctl1_ : ctl1) {
//            ctl2.add(ctl1_.getUser());
//        }
//        //set null các field bị đệ quy thay cho @JsonIgnore
//        for (User userr: ctl2) {
//            userr.setFsu(null);
//            userr.setRole(null);
//            userr.setCreatedClasses(null);
//            userr.setUpdatedClasses(null);
//            userr.setReviewedClasses(null);
//            userr.setApprovedClasses(null);
//            userr.setOtp(null);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject("OK", "Successful", null, ctl2));
//    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    @Override
//    public ResponseEntity<ResponseObject> deActiveClass(String id, String nameStatus) {
//        // check format UUID
//        try {
//            UUID.fromString(id);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new ResponseObject("Not Found", "Unsuccessful, Not correct format ID: " + id, null, null));
//        }
//        System.out.println("kha đã ở đây");
//        TrainingClass trainingClass = trainingClassRepository.findById(UUID.fromString(id)).orElse(null);
//
//        if (trainingClass == null) {
//            System.out.println("kha đã ở đâ11");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObject("Not Found", "Unsuccessful, Not found class by id", null, null));
//        }
//
//        ClassStatus classStatus = classStatusRepository.findByName(nameStatus);
//        classStatus.setTrainingClasses(null);
//
//        trainingClass.setClassStatus(classStatus);
//        trainingClassRepository.save(trainingClass);
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject("OK", "successful, Closed Class", null, null));
//
//    }

    @Override
    public ResponseEntity<ResponseObject> getClassAttendeeLevel(UUID id) {

        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class attendee level by class id", null,
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getAttendeeLevel().getName()));

    }

    @Override
    public ResponseEntity<ResponseObject> getClassFormatType(UUID id) {

        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class format type by class id", null,
                            null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getFormatType().getName()));

    }

    @Override
    public ResponseEntity<ResponseObject> getClassStatus(UUID id) {

        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class status by class id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getClassStatus().getName()));

    }

    @Override
    public ResponseEntity<ResponseObject> getTechnicalGroup(UUID id) {

        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found technical group by class id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getTechnicalGroup().getName()));

    }

    @Override
    public ResponseEntity<ResponseObject> getClassFsu(UUID id) {

        TrainingClass trainingClass = trainingClassRepository.findById(id).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found FSU by class id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, trainingClass.getFsu().getName()));

    }

    @Override
    public ResponseEntity<ResponseObject> getDeliveryPrinciple(UUID syllabusId) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId).orElse(null);
        if (syllabus == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found Syllabus by Syllabus id", null, null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful", null, syllabus.getDeliveryPrinciple()));

    }

	@Override
	public ResponseEntity<ResponseObject> updateStatusClass(UpdateStatusForm updateStatusForm) {
		// check format UUID
	      try {
	          UUID.fromString(updateStatusForm.getId());
	      } catch (Exception e) {
	          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	                  new ResponseObject("Not Found", "Unsuccessful, Not correct format ID: " + updateStatusForm.getId(), null, null));
	      }
		
		TrainingClass trainingClass = trainingClassRepository.findById(UUID.fromString(updateStatusForm.getId())).orElse(null);
        if (trainingClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Not Found", "Unsuccessful, Not found class", null, null));
        }
        ClassStatus classStatus = null;
        switch (updateStatusForm.getStatus()) {
			case "OPENNING":
				classStatus = classStatusRepository.findByName(NAME_STATUS_OPENNING_IN_DATABASE);
		        classStatus.setTrainingClasses(null);

		        trainingClass.setClassStatus(classStatus);
		        trainingClassRepository.save(trainingClass);
				break;
			case "PLANNING":
				classStatus = classStatusRepository.findByName(NAME_STATUS_PLANNING_IN_DATABASE);
		        classStatus.setTrainingClasses(null);

		        trainingClass.setClassStatus(classStatus);
		        trainingClassRepository.save(trainingClass);
				break;
			case "ENDED":
				classStatus = classStatusRepository.findByName(NAME_STATUS_ENDED_IN_DATABASE);
		        classStatus.setTrainingClasses(null);

		        trainingClass.setClassStatus(classStatus);
		        trainingClassRepository.save(trainingClass);
				break;
			case "CLOSED":
				classStatus = classStatusRepository.findByName(NAME_STATUS_CLOSED_IN_DATABASE);
		        classStatus.setTrainingClasses(null);

		        trainingClass.setClassStatus(classStatus);
		        trainingClassRepository.save(trainingClass);
				break;
			default:
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
		                new ResponseObject("Fail", "Can't find status", null, null));
		}
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Successful, "+updateStatusForm.getStatus()+" Class", null, null));
	}

    @Override
    public TrainingClass addTraineeToClass(List<UUID> traineeIds, UUID classId) {
        TrainingClass trainingClass = trainingClassRepository.findById(classId)
                .orElseThrow(() -> new ValidationException("Training class is not existed"));

        List<User> listTrainee = userService.findAllById(traineeIds);
        trainingClass.setAccount_trainee(listTrainee);
        return trainingClass;
    }

}