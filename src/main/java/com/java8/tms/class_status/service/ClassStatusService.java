package com.java8.tms.class_status.service;

import com.java8.tms.common.entity.ClassStatus;

import java.util.List;
import java.util.UUID;

public interface ClassStatusService {
    ClassStatus findByName(String name);
    ClassStatus findById(UUID id);
    List<ClassStatus> findAll();
}
