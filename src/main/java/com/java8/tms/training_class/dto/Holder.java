package com.java8.tms.training_class.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class Holder {
    private List<String> keyword;
    private List<String> location;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date from;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date to;
    private List<String> classTime;
    private List<String> status;
    private List<String> attendeeType;
    private String fsu;
    private String trainer;
}
