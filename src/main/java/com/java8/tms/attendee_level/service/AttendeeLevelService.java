package com.java8.tms.attendee_level.service;

import com.java8.tms.common.entity.AttendeeLevel;

import java.util.List;
import java.util.UUID;

public interface AttendeeLevelService {
     AttendeeLevel findByName(String name);
     AttendeeLevel findById(UUID id);
     List<AttendeeLevel> findAll();
}
