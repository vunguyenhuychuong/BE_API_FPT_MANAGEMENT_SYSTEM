package com.java8.tms.training_program.dto;

import java.util.List;

import com.java8.tms.common.dto.Pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * <p>
 * Class for represent response data for user's request of filtering training program
 * </p>
 *
 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseForFilterTrainingProgram {
	private List<TrainingProgramForFilter> trainingPrograms;
	private Pagination pagination;
}
