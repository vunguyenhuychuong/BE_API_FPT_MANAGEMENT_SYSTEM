package com.java8.tms.user.controller;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.UserDTO;
import com.java8.tms.common.payload.request.UpdateUserProfileForm;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.user.dto.UpdateUserPasswordDTO;
import com.java8.tms.user.service.UserService;
import com.java8.tms.user.service.impl.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/profile")
public class UserProfileController {
    private final UserService userService;
    @Autowired
    private final UserServiceImpl userServiceImpl;

    @GetMapping("")
    @Operation(summary = "for get login user profile")
    public ResponseEntity<ResponseObject> getUserProfile(){
        UserDTO userProfile = userService.getUserLoginProfile();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "Get Profile successfully", null, userProfile));
    }

    @PutMapping("")
    @Operation(summary = "for update user login profile")
    public ResponseEntity<ResponseObject> updateUserProfile(@Valid @RequestBody UpdateUserProfileForm userProfileUpdate){
        UserDTO userResponse = userService.updateUserProfile(userProfileUpdate);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "update user profile successfully", null, userResponse ));
    }

    @Operation(summary = "for update login user image by id")
    @PutMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseObject> updateImageUser(
            @Parameter(description = "user image") @RequestParam(name = "image") MultipartFile image) throws FileNotFoundException {

        if (image.isEmpty()) {
            throw new FileNotFoundException("file image not found");
        }
        UserPrinciple userUpdate = userService.updateUserImage(image);

        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "User image update successfully", null, userUpdate));
    }


    @PutMapping("/update-password")
    @Operation(summary = "for changing login user password")
    public ResponseEntity<ResponseObject> changePassword(@Valid @RequestBody UpdateUserPasswordDTO updateUserPasswordDTO) {
        return userServiceImpl.updatePassword(updateUserPasswordDTO);
    }
}
