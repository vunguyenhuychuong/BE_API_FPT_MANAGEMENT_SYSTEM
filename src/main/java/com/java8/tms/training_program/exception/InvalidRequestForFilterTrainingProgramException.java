package com.java8.tms.training_program.exception;

/**
 * 
 * <p>
 * Exception class returned if there are an invalid input field in request of
 * filter training program form user
 * </p>
 *
 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
 */
public class InvalidRequestForFilterTrainingProgramException extends Exception {

	/**
	 * 
	 * <p>
	 * Constructor for this exception class.
	 * </p>
	 *
	 * @param message {@code String}
	 *
	 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
	 */
	public InvalidRequestForFilterTrainingProgramException(String message) {
		super(message);
	}
}
