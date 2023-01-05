package com.java8.tms.common.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable

public class ProgramSyllabusId implements Serializable {
	// Association id for Association clas ProgramSyllabus

	@Column(name = "training_program_id")
	@Type(type = "org.hibernate.type.UUIDCharType")
    private UUID trainingProgramId;
	
	@Column(name = "syllabus_id")
	@Type(type = "org.hibernate.type.UUIDCharType")
    private UUID syllabusId;
}
