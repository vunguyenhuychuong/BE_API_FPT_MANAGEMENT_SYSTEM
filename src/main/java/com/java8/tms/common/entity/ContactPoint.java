package com.java8.tms.common.entity;

import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    private UUID id;
    private String content;
    private String type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fsu_id", nullable = false)
    private FSU fsu;


}




