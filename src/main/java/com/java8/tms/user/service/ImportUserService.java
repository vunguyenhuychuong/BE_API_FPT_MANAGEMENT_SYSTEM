package com.java8.tms.user.service;

import com.java8.tms.common.entity.User;
import com.java8.tms.common.payload.request.UploadCsvForm;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

public interface ImportUserService {
     String getCurrentPermissionWithUser();
     List<String> getCurrentPermissionWithUserOfRoleInCsvFile(List<User> userList);
     String getEncodeType(InputStream file) throws IOException;
     void export(HttpServletResponse response) throws IOException;
     void createCell(Row row, int columnCount, Object value, CellStyle style);
     void writeHeaderLine();
     List<User> convertFileCsvToListUser(MultipartFile file, UploadCsvForm uploadCsvForm) throws DateTimeParseException, IOException, NumberFormatException, IndexOutOfBoundsException, Exception;
     ResponseEntity<?> validate(MultipartFile file, UploadCsvForm uploadCsvForm);
     ResponseEntity<?> importListUser(List<User> userList);
     ResponseEntity<?> importListUserSkipUserName(List<User> userList);
     ResponseEntity<?> importListUserSkipEmail(List<User> userList);
     ResponseEntity<?> importListUserReplaceEmail(List<User> userList);
     ResponseEntity<?> importListUserSkipUserNameAndEmail(List<User> userList);

}

