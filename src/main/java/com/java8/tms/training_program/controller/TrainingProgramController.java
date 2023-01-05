package com.java8.tms.training_program.controller;

import com.java8.tms.common.dto.Pagination;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.TrainingProgramDTO;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.payload.request.UpdateTrainingProgramForm;
import com.java8.tms.training_program.dto.RequestForFilterTrainingProgram;
import com.java8.tms.training_program.dto.ResponseForFilterTrainingProgram;
import com.java8.tms.training_program.dto.TrainingProgramDetailDTO;
import com.java8.tms.training_program.dto.TrainingProgramForFilter;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.training_program.exception.InvalidRequestForGetTrainingProgramException;
import com.java8.tms.training_program.service.TrainingProgramService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@Log4j2
@RestController
@RequestMapping(value = "/api/v1/training-program")
public class TrainingProgramController {
    private final ModelMapper modelMapper;
    private final TrainingProgramService trainingProgramService;

    public TrainingProgramController(ModelMapper modelMapper, TrainingProgramService trainingProgramService) {
        this.modelMapper = modelMapper;
        this.trainingProgramService = trainingProgramService;
    }

    /*
     * <p>
     * Receive and return training program with pagination based on request from
     * user.
     * </p>
     * @param page
     * @param size
     * @return
     * @author Tung Nguyen
     */
//    @GetMapping("")
    @PreAuthorize("hasAuthority('VIEW_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> getAllTrainingProgram(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size)
            throws InvalidRequestForGetTrainingProgramException {
        return trainingProgramService.getAllTrainingProgram(page, size);
    }

    /**
     * <p>
     * Get training program by id
     * </p>
     *
     * @param sid training program id
     * @return ResponseEntity
     * @author Vien Binh
     */
    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('VIEW_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> getTrainingProgramById(@PathVariable(name = "id") String sid) {
        UUID id;
        try {
            id = UUID.fromString(sid);
        } catch (MethodArgumentTypeMismatchException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                    "Training Program ID is not found", null, null));
        }
        TrainingProgramDetailDTO result = trainingProgramService.getTrainingProgramById(id);
        if (result != null)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(HttpStatus.OK.toString(), "Training Program details successful", null, result));
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                    "Training Program ID is not found", null, null));
        }
    }

    /**
     * <p>
     * Return a filtered training programs list based on request data that user
     * input
     * </p>
     *
     * @param searchValue {@code String}
     * @param status      {@code String}
     * @param sortBy      {@code String}
     * @param sortType    {@code String}
     * @param page        {@code int}
     * @param size        {@code int}
     * @return a response entity
     * @throws InvalidRequestForFilterTrainingProgramException if request data is invalid
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('VIEW_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> filterTrainingProgram(
            @RequestParam(value = "searchValue", required = false) @Parameter(name = "searchValue", description = "Max length of each search value: 50; "
                    + "Max number of search values: 5") String[] searchValue,
            @RequestParam(value = "status", required = false) @Parameter(name = "status", description = "ACTIVE or INACTIVE") String status,
            @RequestParam(value = "sortBy", required = false) @Parameter(name = "sortBy", description = "NAME, CREATEDDATE, CREATEDBY or DURATION") String sortBy,
            @RequestParam(value = "sortType", required = false) @Parameter(name = "sortType", description = "ASC or DESC") String sortType,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size)
            throws InvalidRequestForFilterTrainingProgramException {
        log.info("Start method filterTrainingProgram in TrainingProgramController");

        RequestForFilterTrainingProgram requestData = RequestForFilterTrainingProgram.builder()
                .searchValue(searchValue)
                .status(status)
                .sortBy(sortBy)
                .sortType(sortType)
                .page(page)
                .size(size)
                .build();
        // get filtered result and mapping them as result list and pagination to
        // response
        ResponseForFilterTrainingProgram responseData = trainingProgramService.filterTrainingProgram(requestData);
        List<TrainingProgramForFilter> trainingPrograms = responseData.getTrainingPrograms();
        Pagination pagination = responseData.getPagination();

        log.info("End method filterTrainingProgram in TrainingProgramController");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK.toString(), "Filter successfully", pagination, trainingPrograms));
    }

    /**
     * <p>
     * Return a suggested keyword list for user to choose
     * </p>
     *
     * @param requestData {@code String}
     * @return a suggested keyword list for user to choose
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    @GetMapping("/get-keyword-list/{requestData}")
    @PreAuthorize("hasAuthority('VIEW_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> getKeyWordList(
            @PathVariable @Parameter(name = "requestData", description = "Max length of a keyword: 50") String requestData) {
        log.info("Start method getKeyWordList in TrainingProgramController");

        ResponseEntity<ResponseObject> responseEntity = trainingProgramService.getKeywordList(requestData);

        log.info("End method getKeyWordList in TrainingProgramController");
        return responseEntity;
    }

    /**
     * <p>
     * Get the list of training program
     * </p>
     *
     * @return ResponseEntity - List of training program
     * @author Binh
     */
//    @GetMapping("/")
    @PreAuthorize("hasAuthority('VIEW_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> list() {
        List<TrainingProgramDTO> trainingProgramDTOs = new ArrayList<>();
        for (Object trainingProgram : trainingProgramService.findAll()) {
            trainingProgramDTOs.add(modelMapper.map(trainingProgram, TrainingProgramDTO.class));
        }
        ResponseObject responseObject = new ResponseObject("OK", "list all", null, trainingProgramDTOs);
        log.trace("Entering the method test log");
        log.info("HTTP Error");
        log.error("Error (code )....");
        log.warn("Warn (code) ....");
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    /**
     * <p>
     * Update state of training program
     * </p>
     *
     * @param trainingProgram {@code TrainingProgram}
     * @return ResponseEntity
     * @throws ResourceNotFoundException if training program not found
     * @author Vien Binh, Tran Long, Nguyen Hieu
     */
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('MODIFY_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> updateStatus(@RequestBody UpdateTrainingProgramForm trainingProgram)
            throws ResourceNotFoundException {
        log.info("Begin updating training program status controller");
        return trainingProgramService.updateStatus(trainingProgram);
    }

    /**
     * <p>
     * Delete a training program by changing the status to DELETED
     * </p>
     *
     * @param trainingProgramId id of training program
     * @return ResponseEntity
     * @throws ResourceNotFoundException if training program not found
     * @author Vien Binh
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('MODIFY_TRAINING_PROGRAM')")
    public ResponseEntity<ResponseObject> deleteTrainingProgram(@Schema(example = "2f4b0772-4208-4a74-a3f8-d86b5df0fe4a") @PathVariable(name = "id") UUID trainingProgramId) throws ResourceNotFoundException {
        log.info("Begin deleting training program status controller");
        return trainingProgramService.deleteById(trainingProgramId);
    }
}
