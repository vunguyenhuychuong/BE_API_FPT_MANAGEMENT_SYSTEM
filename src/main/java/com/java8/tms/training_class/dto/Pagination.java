package com.java8.tms.training_class.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Pagination {
    private int page;
    private int limit;
    private int totalPage;

}
