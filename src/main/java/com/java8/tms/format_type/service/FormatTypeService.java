package com.java8.tms.format_type.service;

import com.java8.tms.common.entity.FormatType;

import java.util.List;
import java.util.UUID;

public interface FormatTypeService {
    FormatType findByName(String name);
    FormatType findById(UUID id);
    List<FormatType> findAll();
}
