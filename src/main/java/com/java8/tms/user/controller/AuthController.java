package com.java8.tms.user.controller;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.payload.request.LogoutRequestForm;
import com.java8.tms.common.payload.request.SignInForm;
import com.java8.tms.common.payload.request.TokenRefreshRequestForm;
import com.java8.tms.user.dto.UpdateUserPasswordDTO;
import com.java8.tms.user.service.impl.AuthenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenServiceImpl authenService;

    @Operation(summary = "For login", description = "test")
    @PostMapping("/auth/login")
    @SecurityRequirements
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody SignInForm signInForm) {
        if(signInForm.getEmail().isEmpty() || signInForm.getEmail().isBlank() || signInForm.getEmail() == null){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input email", null, null), HttpStatus.BAD_REQUEST);
        }
        if(signInForm.getPassword().isEmpty() || signInForm.getPassword().isBlank() || signInForm.getPassword() == null){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input password", null, null), HttpStatus.BAD_REQUEST);
        }
        return authenService.login(signInForm);
    }

    @PostMapping("/auth/validation")
    @Operation(summary = "For getting user information after login")
    public ResponseEntity<ResponseObject> reloadUserByJWT() {
        return authenService.validateAccessToken();
    }

    @PostMapping("/auth/accesstoken")
    @Operation(summary = "For getting new access token by refresh token after it expired")
    public ResponseEntity<ResponseObject> refreshAccessToken(HttpServletRequest request, @Valid @RequestBody TokenRefreshRequestForm tokenRefreshRequest) {
        if(tokenRefreshRequest.getRefreshToken() == null || tokenRefreshRequest.getRefreshToken().isEmpty() || tokenRefreshRequest.getRefreshToken().isBlank()){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input refresh token", null, null), HttpStatus.BAD_REQUEST);
        }
        return authenService.refreshAccessToken(request, tokenRefreshRequest);
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "For logout")
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, @Valid @RequestBody LogoutRequestForm logoutRequestForm) {
        if(logoutRequestForm.getRefreshToken() == null || logoutRequestForm.getRefreshToken().isEmpty() || logoutRequestForm.getRefreshToken().isBlank()){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input refresh token", null, null), HttpStatus.BAD_REQUEST);
        }
        return authenService.logout(request, logoutRequestForm);
    }
}

