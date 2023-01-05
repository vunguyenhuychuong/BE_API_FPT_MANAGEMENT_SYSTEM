package com.java8.tms.technical_group.service;

import com.java8.tms.common.entity.TechnicalGroup;

import java.util.List;
import java.util.UUID;

public interface TechnicalGroupService {
	TechnicalGroup findByName(String name);
	TechnicalGroup findById(UUID id);
    List<TechnicalGroup> findAll();
}
