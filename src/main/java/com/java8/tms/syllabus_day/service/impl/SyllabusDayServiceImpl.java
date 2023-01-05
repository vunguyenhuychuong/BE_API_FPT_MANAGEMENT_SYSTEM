package com.java8.tms.syllabus_day.service.impl;

import com.java8.tms.common.repository.SyllabusDayRepository;
import com.java8.tms.syllabus_day.service.SyllabusDayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyllabusDayServiceImpl implements SyllabusDayService {
	@Autowired
	private SyllabusDayRepository syllabusDayRepository;
}
