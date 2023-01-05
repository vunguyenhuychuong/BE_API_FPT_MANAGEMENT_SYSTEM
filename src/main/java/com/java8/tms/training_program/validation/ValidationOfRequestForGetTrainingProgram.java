package com.java8.tms.training_program.validation;

import com.java8.tms.training_program.exception.InvalidRequestForGetTrainingProgramException;
import org.springframework.stereotype.Service;


/**
 * 
 * <p>
 * Handle exception each of cases
 * </p>
 *
 * @author Tung Nguyen
 */
@Service
public class ValidationOfRequestForGetTrainingProgram {

    public void validateRequestForGetTrainingProgram(int page, int size) throws InvalidRequestForGetTrainingProgramException {
        if(page < 1) {
            throw new InvalidRequestForGetTrainingProgramException("Page number must be greater than 0");
        }
        if(size < 1) {
            throw new InvalidRequestForGetTrainingProgramException("Page size must be greater than 0");
        }
    }

}
