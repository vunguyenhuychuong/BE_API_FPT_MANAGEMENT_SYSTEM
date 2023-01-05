package com.java8.tms.common.entity;

import java.util.UUID;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssessmentScheme {

	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
	@Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
	private Double assignment;
	private Double quiz;
	private Double exam;
	private Double gpa;
	private Double finalPoint;
	private Double finalTheory;
	private Double finalPractice;
	// syllabus id
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "syllabus_id", referencedColumnName = "id")
	private Syllabus syllabus;
}
