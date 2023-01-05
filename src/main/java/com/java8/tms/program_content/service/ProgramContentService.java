package com.java8.tms.program_content.service;

import com.java8.tms.common.entity.ProgramContent;

import java.util.List;
import java.util.UUID;

public interface ProgramContentService {
    ProgramContent findByName(String name);
    ProgramContent findById(UUID id);
    List<ProgramContent> findAll();
}
