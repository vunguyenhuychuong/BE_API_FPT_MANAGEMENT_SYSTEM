package com.java8.tms.class_status.service.impl;

import com.java8.tms.common.entity.ClassStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java8.tms.class_status.service.ClassStatusService;
import com.java8.tms.common.repository.ClassStatusRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class ClassStatusServiceImpl implements ClassStatusService {
	@Autowired
	private ClassStatusRepository classStatusRepository;

	@Override
	public ClassStatus findByName(String name) {
		return classStatusRepository.findByName(name);
	}

	@Override
	public ClassStatus findById(UUID id) {
		return classStatusRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Class status is not existed"));
	}

	@Override
	public List<ClassStatus> findAll() {
		return classStatusRepository.findAll();
	}
}
