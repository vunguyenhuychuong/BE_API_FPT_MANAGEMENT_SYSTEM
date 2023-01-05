package com.java8.tms.contact_point.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.repository.ContactPointRepository;
import com.java8.tms.contact_point.service.ContactPointService;

@Repository
public class ContactPointServiceImpl implements ContactPointService{
	@Autowired
	private ContactPointRepository contactPointRepository;
}
