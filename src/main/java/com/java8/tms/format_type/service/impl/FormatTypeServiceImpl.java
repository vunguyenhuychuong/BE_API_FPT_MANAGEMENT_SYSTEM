package com.java8.tms.format_type.service.impl;


import com.java8.tms.common.entity.FormatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java8.tms.common.repository.FormatTypeRepository;
import com.java8.tms.format_type.service.FormatTypeService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class FormatTypeServiceImpl implements FormatTypeService{
	@Autowired
	private FormatTypeRepository formatTypeRepository;

	@Override
	public FormatType findByName(String name) {
		return formatTypeRepository.findByName(name);
	}

	@Override
	public FormatType findById(UUID id) {
		return formatTypeRepository.findById(id).orElseThrow(() -> new ValidationException("Format type is not existed"));
	}

	@Override
	public List<FormatType> findAll() {
		return formatTypeRepository.findAll();
	}
}
