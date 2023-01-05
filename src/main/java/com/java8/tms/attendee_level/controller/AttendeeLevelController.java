package com.java8.tms.attendee_level.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.java8.tms.attendee_level.service.AttendeeLevelService;
import com.java8.tms.common.entity.AttendeeLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/attendee_level")
public class AttendeeLevelController {
    @Autowired
    private AttendeeLevelService attendeeLevelService;

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get attendee level by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        AttendeeLevel attendeeLevel = attendeeLevelService.findById(id);
        return ResponseEntity.ok().body(attendeeLevel);
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @Operation(summary = "for get all attendee level")
    @GetMapping("")
    public ResponseEntity<Object> findAll() {
        List<AttendeeLevel> attendeeLevelList = attendeeLevelService.findAll();
        return ResponseEntity.ok().body(attendeeLevelList);
    }


}
