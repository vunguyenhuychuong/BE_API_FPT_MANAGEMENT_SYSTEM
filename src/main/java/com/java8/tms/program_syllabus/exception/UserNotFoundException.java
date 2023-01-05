package com.java8.tms.program_syllabus.exception;

public class UserNotFoundException extends RuntimeException{

	public UserNotFoundException (String message) {
		super(message);
	}
}
