package com.java8.tms.common.entity;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class FSU {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "fsu")
    @JsonIgnore
    private List<ContactPoint> contactPointList; // 1 - n to contactPoint

    @OneToMany(mappedBy = "fsu")
    @JsonIgnore
    private List<TrainingClass> trainingClassList;

}
