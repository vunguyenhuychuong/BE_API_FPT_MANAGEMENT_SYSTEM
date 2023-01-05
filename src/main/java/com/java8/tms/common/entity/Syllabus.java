package com.java8.tms.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.java8.tms.common.meta.SyllabusStatus;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Syllabus {
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
    private String code;
    private String version;
    //	private String levelId; // fk
    private int attendeeNumber;
    @Column(length = 65555)
    private String technicalRequirement;
    @Column(length = 65555)
    private String courseObjective;
    //	private int deliveryPrincipleId;// fk
    private int days;
    private int hours;
    @Enumerated(EnumType.ORDINAL)
    private SyllabusStatus status;
    private boolean isTemplate;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID createdBy;
    @CreationTimestamp
    private Date createdDate;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID updatedBy;
    @CreationTimestamp
    private Date updatedDate;


    @OneToOne(mappedBy = "syllabus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private AssessmentScheme assessmentScheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "level_id")
    @ToString.Exclude
    private SyllabusLevel syllabusLevel;

    @OneToMany(mappedBy = "syllabus", orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<SyllabusDay> syllabusDays;

    @OneToOne(mappedBy = "syllabus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private DeliveryPrinciple deliveryPrinciple;

    @OneToMany(mappedBy = "syllabus")
    @JsonIgnore
    @ToString.Exclude
    private List<SyllabusUnit> syllabusUnits;

    //Training Syllabus
    @OneToMany(mappedBy = "syllabus")
    @ToString.Exclude
    @JsonManagedReference
    @JsonIgnore
    private List<ProgramSyllabus> programSyllabusAssociation; // relationship association

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Syllabus syllabus = (Syllabus) o;
        return id != null && Objects.equals(id, syllabus.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

