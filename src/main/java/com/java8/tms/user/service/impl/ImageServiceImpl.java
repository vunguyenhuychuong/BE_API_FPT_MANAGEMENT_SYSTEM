package com.java8.tms.user.service.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.java8.tms.user.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/testproject-369415.appspot.com/o/%s?alt=media";

    @Override
    public String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("testproject-369415.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();

        //read path file adminsdk json
        InputStream fileAdmin = getClass().getResourceAsStream("/FirebaseAdminSDK.json");

        //read file FirebaseAdminSdk to connect
        Credentials credentials = GoogleCredentials.fromStream(fileAdmin);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        //return string image path
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    @Override
    public File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    @Override
    public String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public String upload(MultipartFile multipartFile){
        if (multipartFile != null && multipartFile.getContentType() != null && !multipartFile.getContentType().toLowerCase().startsWith("image"))
            throw new MultipartException("File is not Image");
        try {
            assert multipartFile != null;
            String fileName = multipartFile.getOriginalFilename();

            assert fileName != null;
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            File file = this.convertToFile(multipartFile, fileName);
            String TEMP_URL = this.uploadFile(file, fileName);
            file.delete();

            return TEMP_URL;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean deleteImage(String image) throws IOException{
        //split image name from imagePath
        String imageName = splitStringImage(image);
        BlobId blobId = BlobId.of("testproject-369415.appspot.com", imageName);

        //read path file adminsdk json
        InputStream fileAdmin = getClass().getResourceAsStream("/FirebaseAdminSDK.json");

        //read file FirebaseAdminSdk to connect
        Credentials credentials = GoogleCredentials.fromStream(fileAdmin);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        return storage.delete(blobId);
    }

    public String splitStringImage(String imagePath){
        URI uri = URI.create(imagePath);
        String path = uri.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
