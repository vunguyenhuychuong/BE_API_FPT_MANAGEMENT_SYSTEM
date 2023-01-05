package com.java8.tms.authority.controller;


import com.java8.tms.authority.service.AuthorityService;
import com.java8.tms.authority.service.impl.AuthorityServiceImpl;
import com.java8.tms.common.dto.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class AuthorityController {
    private final ModelMapper mapper;
    @Autowired
    private AuthorityServiceImpl authorityService;

    public AuthorityController(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/authorities")
    @PreAuthorize("hasAuthority('MODIFY_USER')")
    public ResponseEntity<ResponseObject> getALlAuthorities(){
        return authorityService.findAllAuthorities();
    }
}
