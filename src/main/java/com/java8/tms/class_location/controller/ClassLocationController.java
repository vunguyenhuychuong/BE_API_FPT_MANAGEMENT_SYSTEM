package com.java8.tms.class_location.controller;

import com.java8.tms.class_location.service.ClassLocationService;
import com.java8.tms.common.entity.ClassLocation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/class_location")
public class ClassLocationController {
    @Autowired
    public ClassLocationService classLocationService;
    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get class location by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        ClassLocation classLocation = classLocationService.findById(id);
        return ResponseEntity.ok().body(classLocation);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all class location")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<ClassLocation> classLocationList = classLocationService.findAll();
        return ResponseEntity.ok().body(classLocationList);
    }

}
