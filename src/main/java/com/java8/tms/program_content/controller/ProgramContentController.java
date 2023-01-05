package com.java8.tms.program_content.controller;

import com.java8.tms.common.entity.ProgramContent;
import com.java8.tms.program_content.service.ProgramContentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/program_content")
public class ProgramContentController {
    @Autowired
    public ProgramContentService programContentService;
    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get program content by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        ProgramContent programContent = programContentService.findById(id);
        return ResponseEntity.ok().body(programContent);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all program content")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<ProgramContent> programContentList = programContentService.findAll();
        return ResponseEntity.ok().body(programContentList);
    }

}
