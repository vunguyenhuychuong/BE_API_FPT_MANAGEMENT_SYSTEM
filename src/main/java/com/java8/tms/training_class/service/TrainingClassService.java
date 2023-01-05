package com.java8.tms.training_class.service;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.payload.request.UpdateClassForm;
import com.java8.tms.training_class.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TrainingClassService {

    List<DataExcelForTrainingClass> readDataFromExcel(MultipartFile file);

//    TrainingClassDTO createTrainingClass(TrainingClassDTO trainingClassDTO);

    ClassResponse getAllClass(int pageNumber, int pageSize, String sortBy, String sortDirection);

    ClassResponse getClass(int pageNumber, int pageSize, String sortBy, String sortType, List<String> searchValue,
                           List<String> location, Date from, Date to, List<String> classTime,
                           List<String> status, List<String> attendeeType, String fsu, String trainer);

    List<String> getSuggestion(String keyword);

    ClassData getAllFieldData();

    ClassDTO getClassById(UUID id);

    TrainingClass update(UUID id, UpdateClassForm updateClassForm);

    TrainingClass createDuplicateClass(UUID id);

    List<String> findAllClassCode();


//    ResponseEntity<ResponseObject> deleteClass(String id, String nameStatus);

//    ResponseEntity<ResponseObject> getClassById(UUID id);

    ResponseEntity<ResponseObject> getClassNameById(UUID id);

    ResponseEntity<ResponseObject> getClassStartTimeById(UUID id);

    ResponseEntity<ResponseObject> getClassCodeById(UUID id);

    ResponseEntity<ResponseObject> getClassStartDateById(UUID id);

    ResponseEntity<ResponseObject> getClassEndDateById(UUID id);

    ResponseEntity<ResponseObject> getClassDurationById(UUID id);

    ResponseEntity<ResponseObject> getClassCreatedByById(UUID id);

    ResponseEntity<ResponseObject> getClassCreatedDateById(UUID id);

    ResponseEntity<ResponseObject> getClassUpdatedByById(UUID id);

    ResponseEntity<ResponseObject> getClassUpdatedDateById(UUID id);

    ResponseEntity<ResponseObject> getClassReviewedByById(UUID id);

    ResponseEntity<ResponseObject> getClassReviewedDateById(UUID id);

    ResponseEntity<ResponseObject> getClassApprovedByById(UUID id);

    ResponseEntity<ResponseObject> getClassEndTimeById(UUID id);

    ResponseEntity<ResponseObject> getClassUniversityCodeById(UUID id);

    ResponseEntity<ResponseObject> getClassPlannedAttendeeById(UUID id);

    ResponseEntity<ResponseObject> getClassAcceptedAttendeeById(UUID id);

    ResponseEntity<ResponseObject> getClassActualAttendeeById(UUID id);

    ResponseEntity<ResponseObject> getClassApprovedDateById(UUID id);

//    ResponseEntity<ResponseObject> deActiveClass(String id, String nameStatus);

    ResponseEntity<ResponseObject> getClassTrainingProgram(UUID id);

    ResponseEntity<ResponseObject> getClassTrainer(UUID id);

    ResponseEntity<ResponseObject> getClassAdmin(UUID id);

    ResponseEntity<ResponseObject> getClassProgramSyllabus(UUID id);

    ResponseEntity<ResponseObject> getClassLocation(UUID id);

//    ResponseEntity<ResponseObject> getClassAttendeeList(UUID id);

    ResponseEntity<ResponseObject> getClassTrainee(UUID id);

    ResponseEntity<ResponseObject> getClassAttendeeLevel(UUID id);

    ResponseEntity<ResponseObject> getClassFormatType(UUID id);

    ResponseEntity<ResponseObject> getClassStatus(UUID id);

    ResponseEntity<ResponseObject> getTechnicalGroup(UUID id);

    ResponseEntity<ResponseObject> getClassFsu(UUID id);

    ResponseEntity<ResponseObject> getDeliveryPrinciple(UUID id);

    ResponseEntity<ResponseObject> viewDetailClass(UUID id);

    ResponseEntity<ResponseObject> updateStatusClass(UpdateStatusForm updateStatusForm);

    TrainingClass addTraineeToClass(List<UUID> traineeIds, UUID classId);
//	ResponseEntity<ResponseObject> getClassCalendar(UUID id);

}
