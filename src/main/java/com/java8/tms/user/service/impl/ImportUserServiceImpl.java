package com.java8.tms.user.service.impl;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;
import com.java8.tms.common.payload.request.UploadCsvForm;
import com.java8.tms.common.repository.AuthorityRepository;
import com.java8.tms.common.repository.RoleRepository;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.common.utils.CsvValidation;
import com.java8.tms.role.service.RoleNotFoundException;
import com.java8.tms.role.service.impl.RoleServiceImpl;
import com.java8.tms.user.service.ImportUserService;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.rmi.UnexpectedException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImportUserServiceImpl implements ImportUserService {
    private static final Log log = LogFactory.getLog(ImportUserServiceImpl.class);
    private final PasswordEncoder passwordEncoder; // Change this
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository UserRepository;
    @Autowired
    CsvValidation csvValidation;
    @Autowired
    AuthorityRepository iAuthorityRepository;
    private XSSFWorkbook workbook;


    // And change the constructor parameter
    @Autowired
    public ImportUserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getCurrentPermissionWithUser() {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (GrantedAuthority grantedAuthority : userPrinciple.getAuthorities()) {
            String str = grantedAuthority.toString();
            if (str.equalsIgnoreCase("FULL_ACCESS_USER"))
                return "FULL_ACCESS_USER";
        }
        return "CREATE_USER";
    }

    @Override
    public List<String> getCurrentPermissionWithUserOfRoleInCsvFile(List<User> userList) {
        List<String> listTmp = new ArrayList<>();
        for (User user : userList) {
            List<String> listPermission = iAuthorityRepository.finAllByRoleIdAndResource(user.getRole().getId().toString(), "USER");
            if (listPermission.contains("FULL_ACCESS")) {
                if (!listTmp.contains(user.getRole().getName()))
                    listTmp.add(user.getRole().getName());
            }
        }
        return listTmp;
    }

    @Override
    public String getEncodeType(InputStream file) throws IOException {
        try {
            BufferedInputStream bis = new BufferedInputStream(file);
            CharsetDetector cd = new CharsetDetector();
            cd.setText(bis);
            CharsetMatch cm = cd.detect();
            return cm.getName();
        } catch (Exception e) {
            throw new IOException("Error while get encoding of file");
        }
    }

    @Override
    public void writeHeaderLine() {
        List<String> roleList = roleRepository.findAllRoleName();
        String[] list = roleList.toArray(new String[0]);
        DataValidation dataValidation;
        DataValidationConstraint constraint;
        DataValidationHelper validationHelper;
        workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Users");
        validationHelper = new XSSFDataValidationHelper(sheet);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1, 5, 5);
        constraint = validationHelper.createExplicitListConstraint(list);
        dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(dataValidation);
        CellRangeAddressList addressList1 = new CellRangeAddressList(1, 1, 2, 2);
        constraint = validationHelper.createExplicitListConstraint(new String[]{"Male", "Female"});
        dataValidation = validationHelper.createValidation(constraint, addressList1);
        dataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(dataValidation);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        sheet.getColumnHelper().setColDefaultStyle(1, cellStyle);
        createCell(row, 0, "Full name", style);
        createCell(row, 1, "Birthday", style);
        createCell(row, 2, "Gender", style);
        createCell(row, 3, "Email", style);
        createCell(row, 4, "Password", style);
        createCell(row, 5, "Role", style);
        createCell(row, 6, "Level", style);
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }

    @Override
    public void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    @Override
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    @Override
    public List<User> convertFileCsvToListUser(MultipartFile file, UploadCsvForm uploadCsvForm) throws DateTimeParseException, IOException, NumberFormatException, IndexOutOfBoundsException {
        List<User> userList = new ArrayList<>();
        String encodeTypeReal = getEncodeType(file.getInputStream());
        if (!uploadCsvForm.getEncodeType().trim().equalsIgnoreCase("autoDetect")) {
            if (!uploadCsvForm.getEncodeType().trim().equalsIgnoreCase(encodeTypeReal)) {
                throw new UnsupportedEncodingException("Encode type does not match");
            }
        }
        CsvParserSettings settings = new CsvParserSettings();
        settings.setSkipEmptyLines(true);
        settings.setDelimiterDetectionEnabled(true);
        settings.setQuoteDetectionEnabled(true);
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setQuoteEscape('\\');
        settings.getFormat().setComment('~');
        settings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(settings);
        List<String[]> allRows = parser.parseAll(new InputStreamReader(file.getInputStream(), encodeTypeReal));
        for (String[] rows : allRows) {
            if (rows[1] == null || !(rows[1].trim().equalsIgnoreCase("fullName"))) {
                if (rows[4] == null) {
                    throw new UnexpectedException("Email cannot be left blank in the csv file");
                }
                // Full name, Birthday, Gender, Phone, Email, Address, Password, Role, Graduated, Gpa, Course name
                User user = User.builder()
                        .fullname(rows[1])
                        .birthday(rows[2] != null ? csvValidation.convertStringToDate(rows[2]) : null)
                        .email(rows[4])
                        .createdDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                        .level(rows[7])
                        .build();
                if (rows[3] != null) {
                    if (!rows[3].trim().equalsIgnoreCase("MALE")) {
                        if (!rows[3].trim().equalsIgnoreCase("FEMALE")) {
                            throw new UnexpectedException("Gender must be male or female");
                        }
                    }
                    user.setGender(rows[3].trim().equalsIgnoreCase("MALE")
                            || rows[3].trim().equalsIgnoreCase("FEMALE") ? Gender.valueOf(rows[3].trim().toUpperCase()) : null);
                }
                if (rows[5] != null) {
                    if (rows[5].length() < 6) {
                        throw new UnexpectedException("Password must be more than 6 characters");
                    }
                    user.setPassword(passwordEncoder.encode(rows[5]));
                } else {
                    user.setPassword(passwordEncoder.encode("123123"));
                }
                if (rows[6] != null) {
                    if (rows[6].trim().equalsIgnoreCase("super_admin")) {
                        throw new UnexpectedException("Do not import role super admin");
                    }
                }
                Role role = roleService.findRoleByName(rows[6]).orElseThrow(() -> new RoleNotFoundException("Role", "Not Found!"));
                user.setRole(role);
                if (role.getName().equalsIgnoreCase("Class_admin")) {
                    user.setStatus(UserStatus.ACTIVE);
                } else if (role.getName().equalsIgnoreCase("Trainer")) {
                    user.setStatus(UserStatus.OFF_CLASS);
                } else {
                    user.setStatus(UserStatus.OFF_CLASS);
                }
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public ResponseEntity<?> validate(MultipartFile file, UploadCsvForm uploadCsvForm) {
        try {
            if (!csvValidation.getExtensionByGuava(file.getOriginalFilename()).equals("csv")) {
                throw new UnexpectedException("Please change the file extension to .csv");
            }
            List<User> userList = convertFileCsvToListUser(file, uploadCsvForm);
            if (getCurrentPermissionWithUser().equals("CREATE_USER")) {
                if (getCurrentPermissionWithUserOfRoleInCsvFile(userList).size() > 0) {
                    boolean firstTime = true;
                    String mes = "You do not have permission to import user with the following role: ";
                    for (String str : getCurrentPermissionWithUserOfRoleInCsvFile(userList)) {
                        if (firstTime) {
                            firstTime = false;
                            mes = mes.concat(str);
                        } else {
                            mes = mes.concat(", ").concat(str);
                        }
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseObject.builder().status(HttpStatus.BAD_REQUEST.toString()).message(mes).build());
                }
            }
            ResponseObject responseObject = new ResponseObject();
            if (uploadCsvForm.getScans().size() > 2) {
                responseObject.setMessage("Type scans is not support");
            }
            if (!uploadCsvForm.getColumnSeparator().trim().equalsIgnoreCase("comma")) {
                if (!uploadCsvForm.getColumnSeparator().trim().equalsIgnoreCase("semiColon")) {
                    responseObject.setMessage(responseObject.getMessage() == null
                            ? "Only support comma and semicolon field separator"
                            : responseObject.getMessage() + ", Only support comma and semicolon field separator");
                }
            }
            if (responseObject.getMessage() != null) {
                responseObject.setStatus(HttpStatus.BAD_REQUEST.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(responseObject);
            }

            switch (uploadCsvForm.getScans().size()) {
                case 0:
                    csvValidation.validUserList(userList, uploadCsvForm);
                    return importListUser(userList);
                case 1:
                    if (uploadCsvForm.getScans().get(0).equalsIgnoreCase("userName")) {
                        switch (uploadCsvForm.getDuplicateHandle().toUpperCase().trim()) {
                            case "ALLOW":
                                csvValidation.validUserList(userList, uploadCsvForm);
                                return importListUser(userList);
                            case "REPLACE":
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Cannot handle user name duplicates using the Replace method")
                                                .build());
                            case "SKIP":
                                csvValidation.validUserList(userList, uploadCsvForm);
                                return importListUserSkipUserName(userList);
                            default:
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Method handle duplicate not support")
                                                .build());
                        }
                    } else if (uploadCsvForm.getScans().get(0).equalsIgnoreCase("userEmail")) {
                        switch (uploadCsvForm.getDuplicateHandle().toUpperCase().trim()) {
                            case "ALLOW":
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Cannot handle email duplicates using the Allow method")
                                                .build());
                            case "REPLACE":
                                csvValidation.validUserList(userList, uploadCsvForm);
                                return importListUserReplaceEmail(userList);
                            case "SKIP":
                                csvValidation.validUserList(userList, uploadCsvForm);
                                return importListUserSkipEmail(userList);
                            default:
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Method handle duplicate not support")
                                                .build());
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResponseObject.builder()
                                        .status(HttpStatus.BAD_REQUEST.toString())
                                        .message("Only supports scanning by user name and email")
                                        .build());
                    }

                case 2:
                    boolean case1 = uploadCsvForm.getScans().get(0).equalsIgnoreCase("userName") && uploadCsvForm.getScans().get(1).equalsIgnoreCase("userEmail");
                    boolean case2 = uploadCsvForm.getScans().get(0).equalsIgnoreCase("userEmail") && uploadCsvForm.getScans().get(1).equalsIgnoreCase("userName");

                    if (case1 || case2) {
                        switch (uploadCsvForm.getDuplicateHandle().toUpperCase().trim()) {
                            case "ALLOW":
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Cannot handle email and user name duplicates using the Allow method")
                                                .build());
                            case "REPLACE":
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Cannot handle email and user name duplicates using the Replace method")
                                                .build());
                            case "SKIP":
                                return importListUserSkipUserNameAndEmail(userList);
                            default:
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseObject.builder()
                                                .status(HttpStatus.BAD_REQUEST.toString())
                                                .message("Method handle duplicate not support")
                                                .build());
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResponseObject.builder()
                                        .status(HttpStatus.BAD_REQUEST.toString())
                                        .message("Only supports scanning by user name and email")
                                        .build());
                    }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            if (e.getMessage().equals("Do not import role super admin")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .message(e.getMessage())
                                .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message(e.getMessage())
                            .build());
        } catch (IndexOutOfBoundsException e) {
            log.error("Wrong format of csv file");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Wrong format of csv file")
                            .build());
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message(e.getMessage())
                            .build());
        } catch (NullPointerException e) {
            log.error("Please enter the correct date format in the csv file(yyyy-mm-dd or dd-mm-yyyy)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Please enter the correct date format in the csv file(yyyy-mm-dd or dd-mm-yyyy)")
                            .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message(e.getMessage())
                            .build());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .message("Error while import file"));
    }

    @Override
    public ResponseEntity<?> importListUser(List<User> userList) {
        for (User user : userList) {
            if (UserRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseObject.builder().status(HttpStatus.BAD_REQUEST.toString()).message("Email in file csv is existed before in database").build());
            } else {
                UserRepository.save(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder().status(HttpStatus.OK.toString()).message("Import user success").build());
    }

    @Override
    public ResponseEntity<?> importListUserSkipUserName(List<User> userList) {
        int count = 0;
        for (User user : userList) {
            List<User> tmpList = UserRepository.findByFullnameLike(user.getFullname());
            if (tmpList.size() == 0)
                UserRepository.save(user);
            else
                count++;
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK.toString())
                        .message("Import User Success, skipped " + count + "/" + userList.size())
                        .build());
    }

    @Override
    public ResponseEntity<?> importListUserSkipEmail(List<User> userList) {
        int count = 0;
        for (User user : userList) {
            Optional<User> tmpUser = UserRepository.findByEmail(user.getEmail());
            if (tmpUser.isPresent())
                count++;
            else
                UserRepository.save(user);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "Import User Success, " + "Skipped " + count + "/" + userList.size(), null, null));
    }

    @Override
    public ResponseEntity<?> importListUserReplaceEmail(List<User> userList) {
        int count = 0;
        for (User user : userList) {
            Optional<User> tmpUser = UserRepository.findByEmail(user.getEmail());
            if (tmpUser.isPresent()) {
                UserRepository.deleteUserByEmail(user.getEmail());
                count++;
            }
            UserRepository.save(user);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "Import User Success, Replace " + count + "/" + userList.size(), null, null));
    }

    @Override
    public ResponseEntity<?> importListUserSkipUserNameAndEmail(List<User> userList) {
        int count = 0;
        for (User user : userList) {
            if (!(UserRepository.findByEmail(user.getEmail()).isPresent() || UserRepository.findByFullnameLike(user.getFullname()).size() > 0)) {
                UserRepository.save(user);
            } else
                count++;

        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "Import User Success, " + "Skipped " + count + "/" + userList.size(), null, null));
    }
}
