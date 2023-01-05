package com.java8.tms.syllabus.controller;

import com.java8.tms.common.dto.ErrorResponse;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.repository.SyllabusRepository;
import com.java8.tms.syllabus.dto.FormSyllabusDTO;
import com.java8.tms.syllabus.dto.FormSyllabusDTOWithoutId;
import com.java8.tms.syllabus.dto.RequestForListOfSyllabus;
import com.java8.tms.syllabus.dto.SyllabusDTO;
import com.java8.tms.syllabus.service.SyllabusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * view syllabus details
 * </p>
 *
 * @author kiet phan
 */

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/syllabus")
public class SyllabusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyllabusController.class);
    @Autowired
    private SyllabusService service;
    @Autowired
    private SyllabusRepository syllabusRepository;

    @PutMapping("/de-active/{id}")
    @PreAuthorize("hasAuthority('MODIFY_SYLLABUS')")
    @Operation(summary = "de-active syllabus by syllabus id ")
    public ResponseObject deactiveSyllabus(@PathVariable UUID id) throws ResourceNotFoundException {
        return service.deactiveSyllabus(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODIFY_SYLLABUS')")
    @Operation(summary = "delete syllabus by syllabus id")
    public ResponseObject deleteSyllabus(@PathVariable UUID id) throws ResourceNotFoundException {
        return service.deleteSyllabus(id);
    }

    @GetMapping("/suggest")
    @PreAuthorize("hasAuthority('VIEW_SYLLABUS')")
    @Operation(summary = "get list keyword to search")
    public ResponseEntity<ResponseObject> getListOfSuggestions(
            @Parameter(description = "Keyword to search (EX: Basic Java, Basic Python)")
            @RequestParam(name = "keyword") @Size(max = 50) String keyword) {
        LOGGER.info("Start method List of Syllabus Names in SyllabusController");
        return service.getSuggestions(keyword);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('VIEW_SYLLABUS')")
    @Operation(summary = "get all syllabuses ")
    public ResponseEntity<ResponseObject> searchSyllabuses(
            @RequestParam(name = "keywords", defaultValue = "") String[] tags,
            @RequestParam(name = "startDate", defaultValue = "") String startDate,
            @RequestParam(name = "endDate", defaultValue = "") String endDate,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false)
            @Parameter(name = "sortBy", description = "NAME or DAYS or CODE or CREATEDDATE or CREATEDBYUSER") String sortBy,
            @RequestParam(value = "sortType", required = false)
            @Parameter(name = "sortType", description = "ASC or DESC") String sortType) {
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(tags, startDate, endDate,
                page, size, sortBy, sortType);
        LOGGER.info("Start method List of Syllabus in SyllabusController");
        return service.getAllSyllabuses(request);
    }

    @GetMapping("/drafts")
    @PreAuthorize("hasAuthority('VIEW_SYLLABUS')")
    @Operation(summary = "get all drafts by id login user ")
    public ResponseEntity<ResponseObject> searchDraft(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortType", defaultValue = "DESC", required = false)
            @Parameter(name = "sortType", description = "ASC or DESC") String sortType) {
        LOGGER.info("Start method List of Syllabus in SyllabusController");
        return service.getAllDraftByUserId(sortType, page, size);
    }

    @Operation(summary = "For getting data of selected syllabus")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SYLLABUS')")
    public ResponseEntity<ResponseObject> getSyllabusDetails(@PathVariable("id") UUID id) {
        return service.viewSyllabusDetails(id);

    }

    @Operation(summary = "For copying data of selected syllabus to a new one in create syllabus page")
    @PostMapping("/duplicate/{id}")
    @PreAuthorize("hasAuthority('MODIFY_SYLLABUS')")
    public ResponseEntity<?> duplicateSyllabusById(@PathVariable("id") UUID id) {
        return service.duplicateSyllabus(id);

    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_SYLLABUS')")
    public ResponseEntity<?> createNewSyllabus(@RequestBody FormSyllabusDTOWithoutId syllabusDTO) {
        LOGGER.info("Start method save in Syllabus Controller");
        String message = "";
        try {
            String syllabusName = syllabusDTO.getName().trim();
            List<Syllabus> sameNameSyllabuses = syllabusRepository.findAllByName(syllabusName);
            for (Syllabus sameNameSyllabus : sameNameSyllabuses) {
                if (syllabusName.equalsIgnoreCase(sameNameSyllabus.getName())){
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ErrorResponse(new Date(), "400 ERROR", "Syllabus with the same name has already existed."));
                };
            }
            SyllabusDTO syllabus = service.createSyllabus(syllabusDTO);
            LOGGER.info(message);
            ResponseObject response = new ResponseObject("200 OK", message, null, syllabus);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            message = "Couldn't Create Syllabus";
            ErrorResponse error = new ErrorResponse(new Date(), "400 ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
        }
    }

    @PostMapping("/draft")
    @PreAuthorize("hasAuthority('CREATE_SYLLABUS')")
    public ResponseEntity<?> saveAsDraft(@RequestBody FormSyllabusDTOWithoutId syllabusDTO) {
        LOGGER.info("Start method save in Syllabus Controller");
        String message = "";
        try {
            SyllabusDTO syllabus = service.saveDraftSyllabus(syllabusDTO);
            LOGGER.info(message);
            ResponseObject response = new ResponseObject("200 OK", message, null, syllabus);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            message = "Couldn't Create Syllabus";
            ErrorResponse error = new ErrorResponse(new Date(), "400 ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
        }
    }

    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('CREATE_SYLLABUS')")
    public ResponseEntity<?> importSyllabus(@RequestParam("File") MultipartFile reapExcelDataFile, @RequestParam("Scanning") List<String> scanning,
                                            @RequestParam("Duplicate handle") String duplicateHandle) {
        LOGGER.info("Start method save in Syllabus Controller");
        String message = "Imported successfully!";
        try {
            service.mapReapExcelDatatoDB(reapExcelDataFile, scanning, duplicateHandle);
            LOGGER.info(message);
            ResponseObject response = new ResponseObject("200 OK", message, null, null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            message = "Invalid file type";
            ErrorResponse error = new ErrorResponse(new Date(), "400 ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
        }
    }

    @Operation(summary = "download syllabus template and guide for create syllabus")
    @GetMapping("/template/download")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public @ResponseBody byte[] downloadXlsxTemplate(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/xlsx");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"SyllabusTemplate.zip\"");
        InputStream file;
        try {
            file = getClass().getResourceAsStream("/template/SyllabusTemplate.zip");
        } catch (Exception e) {
            throw new FileNotFoundException("File template not exist.");
        }
        return IOUtils.toByteArray(file);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateSyllabus(@RequestBody FormSyllabusDTO syllabusDTO) {
        LOGGER.info("Start method update in Syllabus Controller");
        String message = "";
        try {
            SyllabusDTO syllabus = service.updateSyllabus(syllabusDTO);
            LOGGER.info(message);
            ResponseObject response = new ResponseObject("OK", message, null, syllabus);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            message = "Couldn't Update Syllabus";
            ErrorResponse error = new ErrorResponse(new Date(), "ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
        }
    }
}
