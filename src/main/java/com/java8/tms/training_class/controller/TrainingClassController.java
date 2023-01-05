package com.java8.tms.training_class.controller;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.payload.request.UpdateClassForm;
import com.java8.tms.training_class.dto.*;
import com.java8.tms.training_class.service.TrainingClassService;
import com.java8.tms.training_class.service.impl.TrainingClassServiceImpl;
import com.java8.tms.training_class.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.compress.utils.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/training_class")
public class TrainingClassController {
    
    
    private final TrainingClassServiceImpl trainingClassServiceImpl;
    @Autowired
    private TrainingClassService trainingClassService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public TrainingClassController(TrainingClassServiceImpl trainingClassServiceImpl) {
        this.trainingClassServiceImpl = trainingClassServiceImpl;
    }
    
    @Operation(summary = "download class template and guide for create class")
    @GetMapping("/import/template/download")
    @PreAuthorize("hasAuthority('CREATE_CLASS')")
    public @ResponseBody byte[] downloadXlsxTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/xlsx");
        response.addHeader("Content-Disposition", "attachment; filename=\"ClassTemplate.zip\"");
        InputStream file;
        try {
            file = getClass().getResourceAsStream("/template/ClassTemplate.zip");
        }catch (Exception ex) {
            throw new FileNotFoundException("File template not exist.");
        }
        return IOUtils.toByteArray(file);
    }


    /*
     *
     * import training class by file
     *
     * This API will return training class response object if file import has no error
     * it can return two list include classDataOk stand for available class that can be add into training program
     * and classDataError stand for some syllabus that not available add into training class
     * if classDataError not empty it will return BAD REQUEST status, else it will return OK status and add new training class into database
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidCreateProgramException
     * @author Hai Nam
     *
     */
    @Operation(summary = "Import file to create a new training class")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CREATE_CLASS')")
    public List<ResponseObject> importTrainingClass(@RequestPart(required = true) MultipartFile file) {
        List<DataExcelForTrainingClass> list = trainingClassService.readDataFromExcel(file);
        List<ResponseObject> list1 = new ArrayList<>();

        try {
            for (DataExcelForTrainingClass value : list) {
                if (value.getMessageError() == null) {

                    ResponseObject rp = ResponseObject.builder()
                            .status(HttpStatus.OK.toString())
                            .message("Import File Successful")
                            .data(value)
                            .build();
                    list1.add(rp);
                }
                if (value.getMessageError() != null) {
                    ResponseObject rp = ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Import File Failed")
                            .data(value)
                            .build();
                    list1.add(rp);
                }
            }
            return list1;
        } catch (NullPointerException e) {
            return null;
        }
    }
    @PutMapping("/classes/{id}")
    @PreAuthorize("hasAuthority('MODIFY_CLASS')")
    @Operation(summary = "for update training class")
    public ResponseEntity<Object> updateClass(@Parameter(description = "enter class id", required = true)
                                                  @PathVariable("id") UUID id,
                                                  @RequestBody UpdateClassForm updateClassForm
                                              ) {
        try {
            TrainingClass trainingClass = trainingClassService.update(id, updateClassForm);
            TrainingClassUpdateDTO trainingClassResponse = trainingClassServiceImpl.mapTrainingClassToDto(trainingClass);
            return ResponseEntity.ok().body(new ResponseObject("OK","update successful", null, trainingClassResponse));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("Fail", e.getMessage(), null, null));
        }
    }
    @PostMapping("/classes/duplicate/{id}")
    @PreAuthorize("hasAuthority('CREATE_CLASS')")
    @Operation(summary = "for create a duplicate training class")
    public ResponseEntity<Object> createDuplicateClass(@Parameter(description = "enter class id", required = true) @PathVariable("id") UUID id) {
        try {
            TrainingClass trainingClass = trainingClassService.createDuplicateClass(id);
            TrainingClassUpdateDTO trainingClassResponse = trainingClassServiceImpl.mapTrainingClassToDto(trainingClass);
            return ResponseEntity.ok().body(new ResponseObject("OK","create successful", null, trainingClassResponse));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("Fail", e.getMessage(), null, null));
        }
    }
    @PutMapping("/classes/trainee/{traineeIds}/{classId}")
    @PreAuthorize("hasAuthority('MODIFY_CLASS')")
    @Operation(summary = "api for add trainee to class")
    public ResponseEntity<Object> addTraineeToClass(@PathVariable("traineeIds") List<UUID> traineeIds, @PathVariable("classId") UUID classId ) {
        try {
            TrainingClass trainingClass = trainingClassService.addTraineeToClass(traineeIds, classId);
            TrainingClassUpdateDTO trainingClassResponse = trainingClassServiceImpl.mapTrainingClassToDto(trainingClass);
            return ResponseEntity.ok().body(new ResponseObject("OK","create successful", null, trainingClassResponse));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("Fail", e.getMessage(), null, null));
        }
    }

    @GetMapping("/classes/{id}")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public ResponseEntity<ResponseObject> viewDetailClass(@PathVariable("id") UUID id) {
        return trainingClassServiceImpl.viewDetailClass(id);
    }

//    @DeleteMapping("/classes/{id}")
//    public ResponseEntity<ResponseObject> deleteClass(@PathVariable("id") String id) {
//        return trainingClassServiceImpl.deleteClass(id, NAME_STATUS_DELETE_IN_DATABASE);
//    }
//
//    @PutMapping("/classes/de-active/{id}")
//    public ResponseEntity<ResponseObject> deActiveClass(@PathVariable("id") String id) {
//        return trainingClassServiceImpl.deActiveClass(id, NAME_STATUS_DEACTIVE_IN_DATABASE);
//
//    }
    
    @PutMapping("/status")
    public ResponseEntity<ResponseObject> updateStatus(@RequestBody UpdateStatusForm updateStatusForm) {
        return trainingClassServiceImpl.updateStatusClass(updateStatusForm);
        //@RequestParam("Id") String id, @RequestParam("Status") String status
    }

    @GetMapping("/{syllabus_id}/deliveryPrinciple")
    public ResponseEntity<ResponseObject> getDeliveryType(@PathVariable UUID syllabus_id) {
        return trainingClassServiceImpl.getDeliveryPrinciple(syllabus_id);
    }

    /*
     * <p>
     * Receive and return training class with pagination and filter based on request from
     * user.
     * </p>
     * @param pageNumber
     * @param pageSize
     * @param sortBy
     * @param sortDirection
     * @return
     * @author Tran Thanh Hiep - Java 09
     */
    @GetMapping("/getAllClass")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    @Operation(summary = "get all class page filter and paging")
    public ClassResponse getAllClass(
            @Parameter(description = "Page Number (1...)", example = "1") @RequestParam(name = "page") @Min(value = 1, message = "Page number must be greater than or equal to 1") int pageNumber,

            @Parameter(description = "Page size (1...)", example = "1") @RequestParam(name = "size")  @Min(value = 1, message = "Page size must be greater than or equal to 1") int pageSize,

            @Parameter(description = "Sort by (EX: id, courseCode, trainingProgram, start_date, end_date, duration, attendee, status, location, fsu)") @RequestParam( name = "sortBy" ,required = false) String sortBy,

            @Parameter(description = "Get class with sort type (EX: asc,desc)") @RequestParam(name = "sortType", required = false) String sortDirection) throws Exception {

        return trainingClassService.getAllClass(pageNumber, pageSize, sortBy, sortDirection);
    }

    /*
     * <p>
     * Find class by id from user
     * </p>
     * @pathVariable id
     * @return
     * @author Tran Thanh Hiep - Java 09
     */
    @GetMapping(value = "/getClassById/{id}")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(trainingClassService.getClassById(id));
    }

    @GetMapping("/viewClass")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public ClassResponse getClass(
            @Parameter(description = "Page Number (1...)") @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @Parameter(description = "Page size (1...)") @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Sort by (EX: id, courseCode, trainingProgram, start_date, end_date, duration, attendee, status, location, fsu)") @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Get class with sort type (EX: asc,desc)") @RequestParam(value = "sortType", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortType,
            @Parameter(description = "Get list of search value") @RequestParam(value = "searchValue", required = false) List<String> searchValue,
            @Parameter(description = "Get location name: value from ClassData") @RequestParam(value = "location", required = false) List<String> location,
            @Parameter(description = "Get the start date of a class") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "from", required = false) Date from,
            @Parameter(description = "Get the end date of a class") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "to", required = false) Date to,
            @Parameter(description = "Get the class time: Morning (8h-12h), Noon (13h-17h), Night (18h-22h); Get the format type: Online") @RequestParam(value = "classTime", required = false) List<String> classTime,
            @Parameter(description = "Get status name") @RequestParam(value = "status", required = false) List<String> status,
            @Parameter(description = "Get the attendee type") @RequestParam(value = "attendeeType", required = false) List<String> attendeeType,
            @Parameter(description = "Get the fsu: value from ClassData") @RequestParam(value = "fsu", required = false) String fsu,
            @Parameter(description = "Get trainer's id: value from ClassData") @RequestParam(value = "trainer", required = false) String trainer) {
        return trainingClassService.getClass(pageNumber, pageSize, sortBy, sortType, searchValue, location, from, to, classTime,
                status, attendeeType, fsu, trainer);
    }

    @GetMapping("/metadata")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public ClassData getAllFieldData(){
        return trainingClassService.getAllFieldData();
    }

    @GetMapping("/suggestion")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public List<String> getSuggestion(
            @RequestParam(value = "keyword", required = true) String keyword
    ){
        return trainingClassService.getSuggestion(keyword);
    }
}
