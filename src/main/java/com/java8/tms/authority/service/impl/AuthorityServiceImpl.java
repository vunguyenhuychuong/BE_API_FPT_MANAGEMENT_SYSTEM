package com.java8.tms.authority.service.impl;


import com.java8.tms.authority.service.AuthorityService;
import com.java8.tms.common.dto.AuthorityDTO;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Authority;
import com.java8.tms.common.repository.AuthorityRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private final ModelMapper mapper;

    public AuthorityServiceImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public ResponseEntity<ResponseObject> findAllAuthorities(){
        List<Authority> authorities = authorityRepository.findAllByOrderByResource();
        List<AuthorityDTO> authorityDTOList = mapper.map(authorities, new TypeToken<List<Authority>>() {}.getType());
        return new ResponseEntity<>(new ResponseObject(HttpStatus.ACCEPTED.toString(),"Get list authorities success!", null, authorityDTOList ), HttpStatus.ACCEPTED);
    }
}
