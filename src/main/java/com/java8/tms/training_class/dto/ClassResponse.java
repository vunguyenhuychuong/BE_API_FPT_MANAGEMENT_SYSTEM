package com.java8.tms.training_class.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponse {
     private String status;
     private String message;
     private Pagination pagination;
     private List<ClassDTO> data;

}
