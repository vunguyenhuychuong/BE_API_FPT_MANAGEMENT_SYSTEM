package com.java8.tms.syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestForListOfSyllabus {
    //private String searchKeywords;
    private String[] tags;
    private String startDate;
    private String endDate;
    private int page;
    private int size;
    private String sortBy;
    private String sortType;
}
