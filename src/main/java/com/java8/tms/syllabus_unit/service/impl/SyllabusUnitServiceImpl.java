package com.java8.tms.syllabus_unit.service.impl;

import com.java8.tms.common.repository.SyllabusUnitRepository;
import com.java8.tms.syllabus_unit.service.SyllabusUnitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyllabusUnitServiceImpl implements SyllabusUnitService {
	@Autowired
	private SyllabusUnitRepository syllabusUnitRepository;
}
