package com.java8.tms.training_program.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestForFilterTrainingProgram {
    private String[] searchValue;
    private String status;
    private String sortBy;
    private String sortType;
    private int page;
    private int size;

}
