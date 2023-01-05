package com.java8.tms.syllabus_unit_chapter.service.impl;

import com.java8.tms.common.repository.SyllabusUnitChapterRepository;
import com.java8.tms.syllabus_unit_chapter.service.SyllabusUnitChapterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyllabusUnitChapterServiceImpl implements SyllabusUnitChapterService {
	@Autowired
	private SyllabusUnitChapterRepository syllabusUnitChapterRepository;
}
