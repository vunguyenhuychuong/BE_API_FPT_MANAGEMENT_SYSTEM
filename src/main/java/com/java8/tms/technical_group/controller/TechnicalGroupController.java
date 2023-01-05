package com.java8.tms.technical_group.controller;

import com.java8.tms.common.entity.TechnicalGroup;
import com.java8.tms.technical_group.service.TechnicalGroupService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/technical_group")
public class TechnicalGroupController {
    @Autowired
    public TechnicalGroupService technicalGroupService;
    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get technical group by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        TechnicalGroup technicalGroup = technicalGroupService.findById(id);
        return ResponseEntity.ok().body(technicalGroup);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all technical group")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<TechnicalGroup> technicalGroupList = technicalGroupService.findAll();
        return ResponseEntity.ok().body(technicalGroupList);
    }

}
