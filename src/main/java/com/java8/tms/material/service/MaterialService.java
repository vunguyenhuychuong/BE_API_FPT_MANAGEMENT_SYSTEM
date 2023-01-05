package com.java8.tms.material.service;

import com.java8.tms.material.dto.SendParamMaterial;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Service
public interface MaterialService {
	
    ResponseEntity<byte[]> downloadFile(UUID materialId);
    
    ResponseEntity<?> deleteTrainingMaterial(UUID materialId);
    
    ResponseEntity<?> upload(SendParamMaterial param, MultipartFile file);
    
    ResponseEntity<?> update(SendParamMaterial param, MultipartFile file);

}
