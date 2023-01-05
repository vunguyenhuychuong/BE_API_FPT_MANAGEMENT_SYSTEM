package com.java8.tms.program_content.service.impl;

import com.java8.tms.common.entity.ProgramContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java8.tms.common.repository.ProgramContentRepository;
import com.java8.tms.program_content.service.ProgramContentService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class ProgramContentServiceImpl implements ProgramContentService{
	@Autowired
	private ProgramContentRepository programContentRepository;

	@Override
	public ProgramContent findByName(String name) {
		return programContentRepository.findByName(name);
	}

	@Override
	public ProgramContent findById(UUID id) {
		return programContentRepository.findById(id).orElseThrow(() -> new ValidationException("Program content is not existed"));
	}

	@Override
	public List<ProgramContent> findAll() {
		return programContentRepository.findAll();
	}
}
