package com.java8.tms.syllabus_level.service.impl;

import com.java8.tms.common.repository.SyllabusLevelRepository;
import com.java8.tms.syllabus_level.service.SyllabusLevelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyllabusLevelServiceImpl implements SyllabusLevelService {
	@Autowired
	private SyllabusLevelRepository syllabusLevelRepository;
}
