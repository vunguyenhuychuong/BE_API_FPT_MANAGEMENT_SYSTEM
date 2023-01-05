package com.java8.tms.common.entity;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyllabusLevel {
	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
	@Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
	private String name;
    @JsonIgnore
	@OneToMany(mappedBy = "syllabusLevel", orphanRemoval = true)
	List<Syllabus> syllabuses;
}
