package com.java8.tms.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.java8.tms.common.meta.TrainingProgramStatus;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgram {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Column(columnDefinition = "varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL, FULLTEXT KEY name(name)")
    private String name;

    private boolean isTemplate;

    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID createdBy;

    private Date createdDate;

    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID updatedBy;

    private Date updatedDate;
    private String version;
    // private int duration; // day

    @Enumerated(EnumType.ORDINAL)
    private TrainingProgramStatus status;

    @OneToOne // đánh dấu có mối quan hệ 1-1 với Tranning class ở dưới
    @JoinColumn(name = "class_id", unique = true) // Liên kết với nhau qua khóa ngoại class_id
    @JsonIgnore
    private TrainingClass trainingClass;

    // Training Syllabus
    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL)
    @JsonIgnore
    @JsonManagedReference
    @ToString.Exclude
    @OrderBy(value = "position ASC")
    private List<ProgramSyllabus> programSyllabusAssociation; // relationship association

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TrainingProgram that = (TrainingProgram) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}