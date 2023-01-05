package com.java8.tms.output_standard.service.impl;

import com.java8.tms.common.repository.OutputStandardRepository;
import com.java8.tms.output_standard.service.OutputStandardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutputStandardServiceImpl implements OutputStandardService {
	@Autowired
	private OutputStandardRepository outputStandardRepository;
}
