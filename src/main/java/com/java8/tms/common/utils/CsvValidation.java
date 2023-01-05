package com.java8.tms.common.utils;

import com.google.common.io.Files;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.payload.request.UploadCsvForm;
import com.java8.tms.common.repository.RoleRepository;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.role.service.impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.UnexpectedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class CsvValidation {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    UserRepository UserRepository;
    @Autowired
    RoleRepository roleRepository;

    public void validUserList(List<User> userList, UploadCsvForm uploadCsvForm) throws UnexpectedException{
        HashSet<String> hashSet = new HashSet<>();
        for (User user : userList) {
            if (!uploadCsvForm.getScans().contains("userEmail")){
                if (UserRepository.findByEmail(user.getEmail()).isPresent())
                    throw new UnexpectedException("Email in excel file already exists in database");
                if (!hashSet.add(user.getEmail()))
                    throw new UnexpectedException("Email in excel file is duplicate");
            }
            if (!new EmailValidation().validateEmail(user.getEmail()))
                throw new UnexpectedException("Email in excel file is wrong format");
        }
    }

    public String getExtensionByGuava(String filename) {
        return Files.getFileExtension(filename);
    }

    public Date convertStringToDate(String str) throws UnexpectedException {
        try {
            str = str.trim().replace("/", "-");
            LocalDate date = LocalDate.parse(str);
            return java.sql.Date.valueOf(date);
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(str, formatter);
                return java.sql.Date.valueOf(date);
            } catch (Exception f) {
                throw new UnexpectedException("Please enter the correct date format in the csv file(yyyy-mm-dd or dd-mm-yyyy)");
            }
        }
    }
}
