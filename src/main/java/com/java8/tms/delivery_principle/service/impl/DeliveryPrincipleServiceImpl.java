package com.java8.tms.delivery_principle.service.impl;

import com.java8.tms.common.repository.DeliveryPrincipleRepository;
import com.java8.tms.delivery_principle.service.DeliveryPrincipleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPrincipleServiceImpl implements DeliveryPrincipleService {
	@Autowired
	private DeliveryPrincipleRepository deliveryPrincipleRepository;
}
