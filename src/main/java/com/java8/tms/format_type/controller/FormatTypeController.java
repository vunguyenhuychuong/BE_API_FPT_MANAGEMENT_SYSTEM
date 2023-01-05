package com.java8.tms.format_type.controller;

import com.java8.tms.common.entity.FormatType;
import com.java8.tms.format_type.service.FormatTypeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/format_type")
public class FormatTypeController {
    @Autowired
    public FormatTypeService formatTypeService;

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get format type by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        FormatType formatType = formatTypeService.findById(id);
        return ResponseEntity.ok().body(formatType);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all format type")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<FormatType> formatTypeList = formatTypeService.findAll();
        return ResponseEntity.ok().body(formatTypeList);
    }

}
