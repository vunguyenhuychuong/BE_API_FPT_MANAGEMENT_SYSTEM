package com.java8.tms.material.controller;

import com.java8.tms.material.dto.SendParamMaterial;
import com.java8.tms.material.service.MaterialService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;




@RestController
@RequestMapping(value = "/api/v1/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @Operation(summary = "For downloading file of selected material")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SYLLABUS')")
    public ResponseEntity<byte[]> getFileById(@PathVariable("id") UUID materialId) {
        return materialService.downloadFile(materialId);
    }

    @Operation(summary = "For uploading new material",
    		description = "Can input file null or url null but not both.")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('MODIFY_SYLLABUS')")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "unitChapterId", required = true) UUID unitChapterID,
    									@RequestParam(value = "syllabusId", required = true) UUID syllabusId,
    									@RequestParam(value = "url", required = false) String url,
    									@RequestParam(value = "name", required = true) String name,
    									@RequestParam(value = "file", required = false) MultipartFile file) {
    	SendParamMaterial param = new SendParamMaterial(unitChapterID, syllabusId, url, name);
    	return materialService.upload(param, file);
    }

    @Operation(summary = "For update material",
    		description = "Can input file null or url null but not both")
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('MODIFY_SYLLABUS')")
    public ResponseEntity<?> updateFile(@RequestParam(value = "materialId", required = true) UUID materialId,
										@RequestParam(value = "syllabusId", required = true) UUID syllabusId,
										@RequestParam(value = "url", required = false) String url,
										@RequestParam(value = "name", required = true) String name,
										@RequestParam(value = "file", required = false) MultipartFile file) {
    	SendParamMaterial param = new SendParamMaterial(materialId, syllabusId, url, name);
        return materialService.update(param, file);
    }

    @Operation(summary = "For deleting material")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODIFY_SYLLABUS')")
    public ResponseEntity<?> deleteTrainingMaterial(@PathVariable("id") UUID id) {
        return materialService.deleteTrainingMaterial(id);

    }
}
