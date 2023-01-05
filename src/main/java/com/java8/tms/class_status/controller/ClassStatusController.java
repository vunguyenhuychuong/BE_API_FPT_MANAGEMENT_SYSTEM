package com.java8.tms.class_status.controller;

import com.java8.tms.class_status.service.ClassStatusService;
import com.java8.tms.common.entity.AttendeeLevel;
import com.java8.tms.common.entity.ClassStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/class_status")
public class ClassStatusController {
    @Autowired
    public ClassStatusService classStatusService;

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get class status by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        ClassStatus classStatus = classStatusService.findById(id);
        return ResponseEntity.ok().body(classStatus);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all class status")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<ClassStatus> classStatusList = classStatusService.findAll();
        return ResponseEntity.ok().body(classStatusList);
    }

}
