package com.java8.tms.common.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgramSyllabus implements Serializable {
    // Association class for Training program and Syllabus
    @EmbeddedId
    private ProgramSyllabusId id;

    @ManyToOne
    @MapsId("training_program_id")
    @JoinColumn(name = "training_program_id")
    @JsonBackReference
    private TrainingProgram trainingProgram;

    @ManyToOne
    @MapsId("syllabus_id")
    @JoinColumn(name = "syllabus_id")
    @JsonBackReference
    private Syllabus syllabus;
    private int position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProgramSyllabus that = (ProgramSyllabus) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
