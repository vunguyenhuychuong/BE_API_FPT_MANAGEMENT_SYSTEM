package com.java8.tms.csv.service;


import com.java8.tms.common.entity.User;
import com.java8.tms.common.payload.request.UploadCsvForm;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.user.service.impl.ImportUserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@DataJpaTest
public class CsvServiceTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    private final ImportUserServiceImpl csvService = new ImportUserServiceImpl(passwordEncoder);
    private final File currentDirFile = new File(".");
    private final String helper = currentDirFile.getAbsolutePath();

    private final String currentDir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());

    public CsvServiceTest() throws IOException {
    }

    @Test
    public void getEncodeTypeTest() {
        try {
            String filePath = currentDir + "src\\test\\resources\\TemplateForImportUser\\template_import_user.csv";
            File file = new File(filePath);
            InputStream targetStream = new FileInputStream(file);
            Assert.assertEquals("UTF-8", csvService.getEncodeType(targetStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test(expected = Exception.class)
//    public void convertStringToDateTestException() {
//        csvService.convertStringToDate("2022-02-30");
//    }

    public MultipartFile convertFileToMultipartFile(String filePath, String name, String originalFileName) {
        Path path = Paths.get(filePath);
        String contentType = "text/csv";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }

    @Test
    public void convertFileCsvToListUserTest() {
        try {
            String filePath = currentDir + "src\\test\\resources\\TemplateForImportUser\\template_import_user.csv";
            File file = new File(filePath);
            MultipartFile fileMultipartFile = convertFileToMultipartFile(filePath, "template_import_user.csv", file.getName());
            UploadCsvForm uploadCsvForm = UploadCsvForm.builder().encodeType("UTF-8").scans(new ArrayList<>()).build();
            int expected = 2;
            int result = csvService.convertFileCsvToListUser(fileMultipartFile, uploadCsvForm).size();
            Assert.assertEquals(expected, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importListUserWithValidAndNoScansTest() {
        try {
            //Setup
            File currentDirFile = new File(".");
            String helper = currentDirFile.getAbsolutePath();
            String currentDir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());
            String filePath = currentDir + "src\\test\\resources\\TemplateForImportUser\\template_import_user.csv";
            MultipartFile file = (MultipartFile) new File(filePath);
            UploadCsvForm uploadCsvForm = UploadCsvForm.builder().encodeType("UTF-8").scans(new ArrayList<>()).build();
            //Execute
            List<User> userList = csvService.convertFileCsvToListUser(file, uploadCsvForm);
            int result = userRepository.saveAll(userList).size();
            Assert.assertEquals(result, userList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
