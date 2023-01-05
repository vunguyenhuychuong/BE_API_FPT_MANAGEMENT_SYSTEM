package com.java8.tms.training_program.validation;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.java8.tms.training_program.dto.RequestForFilterTrainingProgram;
import com.java8.tms.training_program.dto.TrainingProgramForFilter;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.training_program.meta.AvailableColumnTrainingProgramTableForSort;
import com.java8.tms.training_program.meta.AvailableTrainingProgramStatusForFilter;
import com.java8.tms.training_program.meta.ColumnTrainingProgramTable;

/**
 * 
 * <p>
 * Class use to validate the request that user input for filtering training
 * program
 * </p>
 *
 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
 */
@Service
public class ValidationOfRequestForFilterTrainingProgram {

	public static final int MAX_NUMBER_OF_SEARCHVALUES = 5;
	public static final int MAX_LENGTH_OF_SEARCHVALUE = 50;

	/**
	 * 
	 * <p>
	 * Validate the request that user input for filtering training program
	 * </p>
	 *
	 * @param requestData {@code RequestForFilterTrainingProgram}
	 * @throws InvalidRequestForFilterTrainingProgramException
	 *
	 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
	 */
	public void validateRequestForFilterTrainingProgram(RequestForFilterTrainingProgram requestData)
			throws InvalidRequestForFilterTrainingProgramException {

		// validate searchValue with maximum number of searchValue (5) and length of each (50)
		if (requestData.getSearchValue() != null) {
			if (requestData.getSearchValue().length > MAX_NUMBER_OF_SEARCHVALUES)
				throw new InvalidRequestForFilterTrainingProgramException(
						"The number of search values cannot exceed " + MAX_NUMBER_OF_SEARCHVALUES);

			for (String searchValue : requestData.getSearchValue()) {
				if (searchValue.length() > MAX_LENGTH_OF_SEARCHVALUE)
					throw new InvalidRequestForFilterTrainingProgramException(
							"Maximum length of a search value cannot exceed " + MAX_LENGTH_OF_SEARCHVALUE);
			}
		}

		// validate page number (integer and > 0)
		if (requestData.getPage() <= 0)
				throw new InvalidRequestForFilterTrainingProgramException("Page number must be an integer > 0");

		// validate page size (integer and > 0)
		if (requestData.getSize() <= 0)
				throw new InvalidRequestForFilterTrainingProgramException("Size of page must be an integer > 0");

		// validate training program status from user input
		try {
			if (requestData.getStatus() != null)
				AvailableTrainingProgramStatusForFilter.valueOf(requestData.getStatus().toUpperCase());
		} catch (Exception e) {
			throw new InvalidRequestForFilterTrainingProgramException(
					"Status to filter training programs must be " + getEnumString(AvailableTrainingProgramStatusForFilter.values())
							+ " or leave null if you want to get all training programs with all status");
		}

		// validate training program showed column from user input with type of sort
		// (ascending or descending)
		if (requestData.getSortBy() != null && requestData.getSortType() != null) {
			try {

				if (requestData.getSortBy().equalsIgnoreCase(TrainingProgramForFilter.Fields.createdBy))
					requestData.setSortBy(ColumnTrainingProgramTable.CREATED_BY.toString());
				if (requestData.getSortBy().equalsIgnoreCase(TrainingProgramForFilter.Fields.createdDate))
					requestData.setSortBy(ColumnTrainingProgramTable.CREATED_DATE.toString());
				AvailableColumnTrainingProgramTableForSort.valueOf(requestData.getSortBy().toUpperCase());
				
			} catch (Exception e) {
				throw new InvalidRequestForFilterTrainingProgramException(
						"Column to sort training programs must be "
								+ TrainingProgramForFilter.Fields.name.toUpperCase() + ", "
								+ TrainingProgramForFilter.Fields.createdDate.toUpperCase() + ", "
								+ TrainingProgramForFilter.Fields.createdBy.toUpperCase() + ", "
								+ TrainingProgramForFilter.Fields.duration.toUpperCase()
								+ " or leave null if you don't want to sort");
			}

			if (!(requestData.getSortType().equalsIgnoreCase("asc")
					|| requestData.getSortType().equalsIgnoreCase("desc")))
				throw new InvalidRequestForFilterTrainingProgramException(
						"Order to sort must be ASC, DESC or leave null if you don't want to sort");
		}

	}
	
	/**
	 * 
	 * <p>
	 * Make a string from an enum as a part in the return error message when user
	 * input invalid data
	 * </p>
	 *
	 * @param enumData {@code <T>} as a enum
	 * 
	 * @return a string value from that enum
	 *
	 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
	 */
	public static <T extends Enum<T>> String getEnumString(T[] enumData) {
		String enumString = Arrays.asList(enumData).toString();
		return enumString.substring(1, enumString.length() - 1);
	}
}
