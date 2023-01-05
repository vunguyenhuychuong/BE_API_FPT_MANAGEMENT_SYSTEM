package com.java8.tms.fsu.controller;

import com.java8.tms.common.entity.FSU;
import com.java8.tms.fsu.service.FSUService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/fsu")
public class FSUController {
    @Autowired
    public FSUService fsuService;
    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get fsu by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        FSU fsu = fsuService.findById(id);
        return ResponseEntity.ok().body(fsu);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all fsu")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<FSU> fsuList = fsuService.findAll();
        return ResponseEntity.ok().body(fsuList);
    }

}
