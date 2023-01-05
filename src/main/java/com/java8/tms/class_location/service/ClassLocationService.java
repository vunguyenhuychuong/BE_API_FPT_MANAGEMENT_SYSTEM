package com.java8.tms.class_location.service;

import com.java8.tms.common.entity.ClassLocation;

import java.util.List;
import java.util.UUID;

public interface ClassLocationService {
    ClassLocation findByName(String name);
    ClassLocation findById(UUID id);
    List<ClassLocation> findAll();
}
