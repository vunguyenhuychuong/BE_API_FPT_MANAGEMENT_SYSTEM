package com.java8.tms.attendee_level.service.impl;

import com.java8.tms.common.entity.AttendeeLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java8.tms.attendee_level.service.AttendeeLevelService;
import com.java8.tms.common.repository.AttendeeLevelRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class AttendeeLevelServiceImpl implements AttendeeLevelService{
	@Autowired
	private AttendeeLevelRepository attendeeLevelRepository;

	@Override
	public AttendeeLevel findByName(String name) {
		return attendeeLevelRepository.findByName(name);
	}

	@Override
	public AttendeeLevel findById(UUID id) {
		return attendeeLevelRepository.findById(id).orElseThrow(() -> new ValidationException("Attendee level is not existed"));
	}

	@Override
	public List<AttendeeLevel> findAll() {
		return attendeeLevelRepository.findAll();
	}
}
