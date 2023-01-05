package com.java8.tms.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageService {
    String uploadFile(File file, String fileName) throws IOException;
    File convertToFile(MultipartFile multipartFile, String fileName) throws IOException;
    String getExtension(String fileName);
    String upload(MultipartFile multipartFile);
    Boolean deleteImage(String image) throws IOException;
}
