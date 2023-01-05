package com.java8.tms.technical_group.service.impl;

import com.java8.tms.common.entity.ClassStatus;
import com.java8.tms.common.entity.TechnicalGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java8.tms.common.repository.TechnicalGroupRepository;
import com.java8.tms.technical_group.service.TechnicalGroupService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class TechnicalGroupServiceImpl implements TechnicalGroupService{
	@Autowired
	private TechnicalGroupRepository technicalGroupRepository;

	@Override
	public TechnicalGroup findByName(String name) {
		return technicalGroupRepository.findByName(name);
	}

	@Override
	public TechnicalGroup findById(UUID id) {
		return technicalGroupRepository.findById(id).orElseThrow(() -> new ValidationException("Technical group is not existed"));
	}

	@Override
	public List<TechnicalGroup> findAll() {
		return technicalGroupRepository.findAll();
	}
}
