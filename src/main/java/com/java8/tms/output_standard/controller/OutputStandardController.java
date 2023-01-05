package com.java8.tms.output_standard.controller;

import com.java8.tms.common.entity.OutputStandard;
import com.java8.tms.common.repository.OutputStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/output_standard")
public class OutputStandardController {
    @Autowired
    OutputStandardRepository outputStandardRepository;
    @GetMapping
    public List<OutputStandard> getAllOutputStandard(){
        return outputStandardRepository.findAll();
    }
}
