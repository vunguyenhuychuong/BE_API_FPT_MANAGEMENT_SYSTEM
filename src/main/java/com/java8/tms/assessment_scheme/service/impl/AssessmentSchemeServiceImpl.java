package com.java8.tms.assessment_scheme.service.impl;

import com.java8.tms.assessment_scheme.service.AssessmentSchemeService;
import com.java8.tms.common.repository.AssessmentSchemeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentSchemeServiceImpl implements AssessmentSchemeService {
	@Autowired
	private AssessmentSchemeRepository assessmentSchemeRepository;
}
