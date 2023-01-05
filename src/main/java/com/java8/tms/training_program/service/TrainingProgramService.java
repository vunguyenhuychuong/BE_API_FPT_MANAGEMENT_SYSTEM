package com.java8.tms.training_program.service;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.payload.request.UpdateTrainingProgramForm;
import com.java8.tms.training_program.dto.RequestForFilterTrainingProgram;
import com.java8.tms.training_program.dto.ResponseForFilterTrainingProgram;
import com.java8.tms.training_program.dto.TrainingProgramDetailDTO;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.training_program.exception.InvalidRequestForGetTrainingProgramException;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TrainingProgramService {

    /*
     *
     * <p>
     * Return a response entity included:
     * status,
     * message,
     * pagination,
     * data
     * of list training program based on request from user
     * </p>
     *
     * @param page
     * @param size
     * @return
     *
     * @author Tung Nguyen
     */
    ResponseEntity<ResponseObject> getAllTrainingProgram(int page, int size)
            throws InvalidRequestForGetTrainingProgramException;

    Collection<TrainingProgram> findAll();

    TrainingProgramDetailDTO getTrainingProgramById(UUID id);

    /**
     * <p>
     * Update state of training program (Active/Inactive)
     * </p>
     *
     * @param trainingProgramForm {@code UpdateTrainingProgramForm}
     * @return ResponseEntity
     * @throws ResourceNotFoundException if training program not found
     * @author Vien Binh, Tran Long, Nguyen Hieu
     */
    ResponseEntity<ResponseObject> updateStatus(UpdateTrainingProgramForm trainingProgramForm) throws ResourceNotFoundException;

    /**
     * <p>
     * Return a filtered training programs list based on request data that user
     * input
     * </p>
     *
     * @param requestData {@code RequestForFilterTrainingProgram} to filter training
     *                    programs
     * @return a response object with result list of training program and pagination
     * @throws InvalidRequestForFilterTrainingProgramException
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    ResponseForFilterTrainingProgram filterTrainingProgram(RequestForFilterTrainingProgram requestData)
            throws InvalidRequestForFilterTrainingProgramException;

    /**
     * <p>
     * Return a matching keyword list for choosing when user enter a keyword
     * </p>
     *
     * @param keyword {@code String}
     * @return a matching keyword list
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    ResponseEntity<ResponseObject> getKeywordList(String keyword);

    Optional<TrainingProgram> findById(UUID id);

    /**
     * <p>
     * Delete a training program by changing the status to DELETED
     * </p>
     *
     * @param id {@code UUID}
     * @return ResponseEntity
     * @throws ResourceNotFoundException if training program not found
     * @author Vien Binh
     */
    ResponseEntity<ResponseObject> deleteById(UUID id) throws ResourceNotFoundException;

}
