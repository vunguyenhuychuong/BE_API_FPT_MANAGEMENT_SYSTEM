package com.java8.tms.user.controller;

import com.google.gson.Gson;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.payload.request.UploadCsvForm;
import com.java8.tms.common.utils.CsvValidation;
import com.java8.tms.user.service.ImportUserService;
import com.java8.tms.user.service.UserService;
import com.java8.tms.user.service.impl.SignupServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/import")
public class UserImportController {
    private final ImportUserService importUserService;
    @Operation(summary = "download user template and guide for create user")
    @GetMapping("/template/download")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public @ResponseBody byte[] downloadCsvTemplate(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"UserTemplate.zip\"");
        InputStream file;
        try {
            file = getClass().getResourceAsStream("/template/UserTemplate.zip");
        } catch (Exception e) {
            throw new FileNotFoundException("File template not exist");
        }
        return IOUtils.toByteArray(file);
    }


    @Operation(summary = "import template to create user")
    @PostMapping(value = "/template/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Require fileName.csv")
            @RequestParam MultipartFile file,
            @Parameter(description = "{\n" +
                    "\"encodeType\":\"autoDetect\",\n" +
                    "\"columnSeparator\":\"comma\",\n" +
                    "\"duplicateHandle\":\"allow\",\n" +
                    "\"scans\": []\n" +
                    "}", required = true)
            @RequestParam(name = "uploadCsvForm") String data) {
        UploadCsvForm uploadCsvForm;
        try {
            Gson gson = new Gson();
            uploadCsvForm = gson.fromJson(data, UploadCsvForm.class);
            return importUserService.validate(file, uploadCsvForm);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Wrong request format")
                            .build());
        }
    }
}
