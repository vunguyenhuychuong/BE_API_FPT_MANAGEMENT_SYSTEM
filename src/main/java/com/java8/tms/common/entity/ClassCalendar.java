package com.java8.tms.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
public class ClassCalendar {

    @Id
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "class_id", nullable = true)
    private TrainingClass trainingClass;

    private int day_no;

    private LocalDateTime dateTime;

    private LocalTime beginTime;

    private LocalTime endTime;

}
