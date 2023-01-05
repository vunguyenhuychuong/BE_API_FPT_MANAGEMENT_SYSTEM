package com.java8.tms.fsu.service.impl;

import com.java8.tms.common.entity.FSU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java8.tms.common.repository.FSURepository;
import com.java8.tms.fsu.service.FSUService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;

@Service
public class FSUServiceImpl implements FSUService{
	@Autowired
	private FSURepository fsuRepository;

	@Override
	public FSU findByName(String name) {
		return fsuRepository.findByName(name);
	}

	@Override
	public FSU findById(UUID id) {
		return fsuRepository.findById(id).orElseThrow(() -> new ValidationException("FSU is not existed"));
	}
	@Override
	public List<FSU> findAll() {
		return fsuRepository.findAll();
	}
}
