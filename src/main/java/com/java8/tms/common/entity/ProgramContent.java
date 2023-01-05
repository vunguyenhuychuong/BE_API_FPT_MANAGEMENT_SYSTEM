package com.java8.tms.common.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "programContent")
    @JsonIgnore
    private Set<TrainingClass> trainingClasses = new HashSet<>();

}
