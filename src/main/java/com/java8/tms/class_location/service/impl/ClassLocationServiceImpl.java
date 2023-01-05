package com.java8.tms.class_location.service.impl;

import com.java8.tms.class_location.service.ClassLocationService;
import com.java8.tms.common.repository.ClassLocationRepository;
import com.java8.tms.common.entity.ClassLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class ClassLocationServiceImpl implements ClassLocationService {
	@Autowired
	private ClassLocationRepository classLocationRepository;

	public void save() {
		ClassLocation classLocation = new ClassLocation();
		classLocation.setName("abc abc abc");
		classLocationRepository.save(classLocation);
	}

	@Override
	public ClassLocation findByName(String name) {
		return classLocationRepository.findByName(name);
	}

	@Override
	public ClassLocation findById(UUID id) {
		return classLocationRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Class location is not existed"));
	}

	@Override
	public List<ClassLocation> findAll() {
		return classLocationRepository.findAll();
	}
}
