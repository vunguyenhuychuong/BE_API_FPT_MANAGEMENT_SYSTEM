package com.java8.tms.training_program.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * <p>
 * Class for represent training programs in table view for user
 * </p>
 *
 * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
public class TrainingProgramForFilter {
	private UUID id;
	private String name;
	private String createdDate;
	private String createdBy;
	private int duration;
	private String status;
}
