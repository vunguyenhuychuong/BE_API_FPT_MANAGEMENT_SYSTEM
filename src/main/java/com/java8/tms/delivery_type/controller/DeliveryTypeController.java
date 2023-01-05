package com.java8.tms.delivery_type.controller;

import com.java8.tms.common.entity.DeliveryType;
import com.java8.tms.common.repository.DeliveryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/delivery_type")
public class DeliveryTypeController {
    @Autowired
    DeliveryTypeRepository deliveryTypeRepository;
    @GetMapping
    public List<DeliveryType> getAllDeliveryType(){
        return deliveryTypeRepository.findAll();
    }
}
