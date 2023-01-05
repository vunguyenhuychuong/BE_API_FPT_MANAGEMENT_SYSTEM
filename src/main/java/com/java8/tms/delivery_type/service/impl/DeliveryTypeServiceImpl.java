package com.java8.tms.delivery_type.service.impl;

import com.java8.tms.common.repository.DeliveryTypeRepository;
import com.java8.tms.delivery_type.service.DeliveryTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryTypeServiceImpl implements DeliveryTypeService {
	@Autowired
	private DeliveryTypeRepository deliveryTypeRepository;
}
