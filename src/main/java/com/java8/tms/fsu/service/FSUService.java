package com.java8.tms.fsu.service;

import com.java8.tms.common.entity.FSU;

import java.util.List;
import java.util.UUID;

public interface FSUService {
    FSU findByName(String name);
    FSU findById (UUID id);
    List<FSU> findAll();
}
