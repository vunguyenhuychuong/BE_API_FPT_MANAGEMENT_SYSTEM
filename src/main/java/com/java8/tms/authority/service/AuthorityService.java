package com.java8.tms.authority.service;

import com.java8.tms.common.dto.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface AuthorityService {
    ResponseEntity<ResponseObject> findAllAuthorities();
}
