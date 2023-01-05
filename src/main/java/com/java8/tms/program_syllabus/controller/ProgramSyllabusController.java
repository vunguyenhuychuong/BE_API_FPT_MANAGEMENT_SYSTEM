package com.java8.tms.program_syllabus.controller;


import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.User;
import com.java8.tms.program_syllabus.dto.*;
import com.java8.tms.program_syllabus.exception.InvalidCreateProgramException;
import com.java8.tms.program_syllabus.exception.InvalidRequestForSaveProgramException;
import com.java8.tms.program_syllabus.service.ProgramSyllabusService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/program_syllabus")
@Log4j2
public class ProgramSyllabusController {


    @Autowired
    private ProgramSyllabusService programSyllabusService;


    /*
     *
     * import training program by file
     * XXX
     * This API will return training program response object if file import has no error
     * it can return two list include syllabusDataOk stand for available syllabus that can be add into training program
     * and syllabusDataError stand for some syllabus that not available add into training program
     *if syllabusDataError not empty it will return BAD REQUEST status, else it will return OK status and add new training program into database
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidCreateProgramException
     * @author Nguyen Quoc Bao
     *
     */
    @Operation(summary = "Import file to create a new training program")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @PostMapping(value = "/file/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importFile(@RequestPart(required = true) MultipartFile file) throws IOException, InvalidCreateProgramException {

        log.info("Start method importFile - ProgramSyllabusController");
        // call method saveListOfTrainingProgram
        SaveProgramResponse res = programSyllabusService.readTrainingProgram(file);
        try {
            // check list syllabus size = list syllabus size after checking
            if (!res.getSyllabusData().getSyllabusOk().isEmpty() && res.getSyllabusData().getSyllabusError().isEmpty()) {
                programSyllabusService.saveTrainingProgram(res);
                log.info("End method importFile - ProgramSyllabusController - Import Successful");
                return ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK.toString())
                                .message("Import File Successful")
                                .data("")
                                .build()
                );
            } else {
                log.info("End method importFile - ProgramSyllabusController - Import Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                        (ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .message("Import File Failed")
                                .data(res)
                                .build()
                        );
            }
        } catch (NullPointerException e) {
            log.info("End method importFile - ProgramSyllabusController - Import Failed");
            throw new InvalidCreateProgramException("Invalid file input, file input must be under 1 MB and correct file excel format(xlsx)");


        }
    }


    /*
     * <p>
     * Download template file excel, the file is used to create New Program
     * </p>
     *
     * @param
     * @return Response message with byte array from the file or error message
     *
     * @author Chu Quang Du
     */
    @Operation(summary = "Download template file to create a new training program")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @GetMapping("/file/download")
    public ResponseEntity<?> downloadTemplateFile() {
        log.info("Start method downloadTemplateFile - ProgramSyllabusController");
        // Calling file download service
        ByteArrayResource templateResource = programSyllabusService.getTemplateResource();

        if (Objects.equals(templateResource, null)) {
            // not found url or file

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .message("A template file not found")
                            .build()
                    );
        }
        //Initializing HTTP headers"
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TrainingProgramTemplate.xlsx");
        log.info("Start method downloadTemplateFile - ProgramSyllabusController");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(templateResource);
    }


    /*
     *
     * <p>
     * Get detailed information of Program with Status: Draft
     * </p>
     *
     * @param programID
     * @param userID
     * @return DetailDraftProgramResponse
     *
     * @author Le Tran Quang Linh
     */
    @Operation(summary = "Get Draft Program ")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @GetMapping("/drafts/{programID}")
    public ResponseEntity<?> getDraftProgram(@PathVariable UUID programID) {
        log.info("Start method getDraftProgram - ProgramSyllabusController");

        User user = programSyllabusService.getUserFromContext();

        // call method getDraftProgram
        DetailDraftProgramResponse programResponse = programSyllabusService.getDraftProgram(programID, user.getId());
        log.info("End method getDraftProgram - ProgramSyllabusController");
        // return if program is found
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.toString())
                        .message("Get Draft Successful")
                        .data(programResponse)
                        .build()
        );
    }

    /*
     *
     * <p>
     * Response a listDraft by any type with ' ResponseEntity<?> ' get a method by
     * access programSyllabus and method is 'mapToResponseForGetListDraftProgram'
     * and method 'mapToResponseForGetListDraftProgram' inject a list TraningProgram
     * by method ' getListDraftOfTrainingProgram'(userID and status syllabus is
     * draft)
     * </p>
     *
     * @param userID
     * @return List<DraftProgramResponse>
     *
     * @author Nguyen Quang Nhat
     */
    @Operation(summary = "Get List Drafts Program")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @GetMapping("/drafts")
    public ResponseEntity<?> getListDraftProgram() {
        log.info("Start method getListDraftProgram  - ProgramSyllabusController");

        User user = programSyllabusService.getUserFromContext();

        // call method getListDraftProgramByUserID
        List<DraftProgramResponse> drafts = programSyllabusService.getListDraftProgramByUserID(user.getId());
        // set up message response
        String messageResponse = "Get List Draft Successful";
        if (drafts.isEmpty()) {
            // no record, reset message response
            messageResponse = "User has no draft";
        }
        log.info("End method getListDraftProgram  - ProgramSyllabusController");
        // return
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.toString())
                        .message(messageResponse)
                        .data(drafts)
                        .build()
        );
    }

    /*
     *
     * <p>
     * Find Syllabus with keyword ( syllabus name + syllabus version)
     * </p>
     *
     * @param keyword
     * @return List <SyllabusResponse>
     *
     * @author Nguyen Gia Bao
     */
    @Operation(summary = "Search syllabus by keyword, use to create a new training program"
            , description = "Keyword: syllabus name + version"
            + "<br/> "
            + "Return only 5 records")
    @PreAuthorize("hasAuthority('MODIFY_TRAINING_PROGRAM')")
    @GetMapping("/syllabuses/search/{keyword}")
    public ResponseEntity<?> searchSyllabusByKeyword(@PathVariable String keyword) {
        log.info("Start method searchSyllabusByKeyword  - ProgramSyllabusController");

        // check keyword length
        if (keyword.trim().length() == 0) {
            throw new InvalidRequestForSaveProgramException("Please enter the keyword to search!");
        }
        // set up message response
        String messageResponse = "Search Syllabus Successful";

        // call method searchSyllabusByKeyword
        List<SyllabusResponse> syllabuses = programSyllabusService.searchSyllabusByKeyword(keyword);
        if (syllabuses.isEmpty()) {
            // no record, reset message response
            messageResponse = "No record with keyword " + keyword;
        }
        log.info("End method searchSyllabusByKeyword  - ProgramSyllabusController");
        // return
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.toString())
                        .message(messageResponse)
                        .data(syllabuses)
                        .build()
        );
    }


    /*
     *
     * <p>
     * Save Program with Status: INACTIVE
     * </p>
     *
     * @param requestProgram
     * @return SaveProgramResponse -  data error
     * @return null - save successful
     *
     * @author Trinh Phan Duc Cuong
     */
    @Operation(summary = "Save Training Program with status INACTIVE"
            , description = "Program name must be between 5-100 characters <br/>"
            + "List syllabuses must be between 1-10 UUID<br/>"
            + "If ID is not present -> Save by Create New Program <br/>"
            + "If ID is present -> Save by Modify Draft Program")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @PostMapping("/complete-program")
    public ResponseEntity<?> saveProgram(@Validated @RequestBody ProgramRequest requestProgram) {

        log.info("Start method saveProgram - ProgramSyllabusController");

        User user = programSyllabusService.getUserFromContext();

        // check list syllabus of requestProgram > 0
        if (requestProgram.getSyllabuses().isEmpty()) {
            throw new InvalidRequestForSaveProgramException("List Syllabus is Empty");
        }

        // remove space of program name
        requestProgram.setName(requestProgram.getName().trim());
//		saveCompleteProgram
        // call Save Program with Status Draft
        SaveProgramResponse program =
                programSyllabusService.saveCompleteProgram(requestProgram, user.getId());

        if (Objects.equals(program, null)) {
            log.info("End method saveProgram - ProgramSyllabusController - Successful");
            // save successful
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.toString())
                            .message("Save Successful")
                            .build()
            );
        }
        log.info("End method saveProgram - ProgramSyllabusController - Failed");
        // save failed - syllabus error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .message("Save Failed")
                        .data(program)
                        .build()
        );
    }


    /*
     *
     * <p>
     * Save Program with Status: DRAFT
     * </p>
     *
     * @param requestProgram
     * @return SaveProgramResponse -  data error
     * @return null - save successful
     *
     * @author Luu Thanh Huy
     */
    @Operation(summary = "Save Training Program with status DRAFT"
            , description = "Program name must be between 5-100 characters <br/>"
            + "List syllabuses must be between 0-10 UUID<br/>"
            + "If ID is not present -> Save by Create New Program <br/>"
            + "If ID is present -> Save by Modify Draft Program <br/>"
            + "Each user can only save 10 draft programs")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @PostMapping("/draft-program")
    public ResponseEntity<?> saveDraftProgram(@Validated @RequestBody ProgramRequest requestProgram) {

        log.info("Start method saveDraftProgram - ProgramSyllabusController");

        User user = programSyllabusService.getUserFromContext();

        // remove space of program name
        requestProgram.setName(requestProgram.getName().trim());

        // call Save Program with Status Draft
        SaveProgramResponse program =
                programSyllabusService.saveDraftProgram(requestProgram, user.getId());

        if (Objects.equals(program, null)) {
            log.info("End method saveDraftProgram - ProgramSyllabusController - Successful");
            // save successful
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.toString())
                            .message("Save Draft Successful")
                            .build()
            );
        }
        log.info("End method saveDraftProgram - ProgramSyllabusController - Failed");
        // save failed - syllabus error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .message("Save Draft Failed")
                        .data(program)
                        .build()
        );

    }

    /*
     *
     * <p>
     * Delete Draft Program, delete all information from Database
     * </p>
     *
     * @param programID
     * @param userID
     * @return message (Successful || Failed)
     *
     * @author Nguyen Minh Tam
     */
    @Operation(summary = "Delete draft program")
    @PreAuthorize("hasAuthority('CREATE_TRAINING_PROGRAM')")
    @DeleteMapping("/draft-programs/{programID}")
    public ResponseEntity<?> deleteDraftProgram(
            @PathVariable UUID programID) {
        log.info("Start method deleteDraftProgram - ProgramSyllabusController");

        User user = programSyllabusService.getUserFromContext();

        // call method delete draft program
        boolean result = programSyllabusService.deleteDraftProgram(user.getId(), programID);
        if (result) {
            log.info("End method deleteDraftProgram - ProgramSyllabusController - Successful");
            // delete successful
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.toString())
                            .message("Delete Draft Program Successful")
                            .data(null)
                            .build()
            );
        } else {
            log.info("End method deleteDraftProgram - ProgramSyllabusController - Failed");
            // delete failed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                            .message("Delete Draft Program Failed")
                            .data(null)
                            .build()
            );
        }
    }

    /*
     *
     * <p>
     * Get detailed information of Program
     * </p>
     *
     * @param programID
     * @return DetailDraftProgramResponse
     *
     * @author Luu Thanh Huy
     */
    @Operation(summary = "Get Program (INACTIVE-ACTIVE - use to duplicate program")
    @PreAuthorize("hasAuthority('MODIFY_TRAINING_PROGRAM')")
    @GetMapping("/programs/{programID}")
    public ResponseEntity<?> getProgram(@PathVariable UUID programID) {
        log.info("Start method getProgram - ProgramSyllabusController");

        // call method getDraftProgram
        DetailDraftProgramResponse programResponse = programSyllabusService.getProgram(programID);
        log.info("End method getProgram - ProgramSyllabusController");
        // return if program is found
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.toString())
                        .message("Get Program Successful")
                        .data(programResponse)
                        .build()
        );
    }


    /*
     *
     * <p>
     * Edit Program
     * </p>
     *
     * @param requestProgram
     * @return SaveProgramResponse -  data error
     * @return null - save successful
     *
     * @author Luu Thanh Huy
     */
    @Operation(summary = "Edit Training Program"
            , description = "Program name can not be edited <br/>"
            + "Can only edit program with INACTIVE, ACTIVE status<br/>"
            + "List syllabuses must be between 1-10 UUID <br/>")
    @PreAuthorize("hasAuthority('MODIFY_TRAINING_PROGRAM')")
    @PutMapping("/program")
    public ResponseEntity<?> editProgram(@Validated @RequestBody EditProgramRequest requestProgram) {

        log.info("Start method editProgram - ProgramSyllabusController");

        User user = programSyllabusService.getUserFromContext();

        // call Save Program with Status Draft
        SaveProgramResponse program =
                programSyllabusService.editProgram(requestProgram, user.getId());

        if (Objects.equals(program, null)) {
            log.info("End method editProgram - ProgramSyllabusController - Successful");
            // save successful
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.toString())
                            .message("Edit Program Successful")
                            .build()
            );
        }
        log.info("End method editProgram - ProgramSyllabusController - Failed");
        // save failed - syllabus error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .message("Edit Program Failed")
                        .data(program)
                        .build()
        );

    }
}
