package com.java8.tms.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPage {
    private int pageNumber =0;
    private int pageSize = 10;
    private String sortDirection;
    private String sortBy;
}
