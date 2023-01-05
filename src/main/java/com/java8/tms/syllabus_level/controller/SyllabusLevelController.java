package com.java8.tms.syllabus_level.controller;

import com.java8.tms.common.entity.SyllabusLevel;
import com.java8.tms.common.repository.SyllabusLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/syllabus_level")
public class SyllabusLevelController {
    @Autowired
    SyllabusLevelRepository syllabusLevelRepository;

    @GetMapping()
    public List<SyllabusLevel> getAllSyllabusLevel(){
        return syllabusLevelRepository.findAll();
    }
}
