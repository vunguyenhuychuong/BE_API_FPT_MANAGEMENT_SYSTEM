package com.java8.tms.user.service;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.payload.request.LogoutRequestForm;
import com.java8.tms.common.payload.request.SignInForm;
import com.java8.tms.common.payload.request.TokenRefreshRequestForm;
import com.java8.tms.user.dto.UpdateUserPasswordDTO;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface AuthenService {
    ResponseEntity<ResponseObject> validateLoginForm(SignInForm signInForm);
    ResponseEntity<ResponseObject> login(SignInForm signInForm);

    ResponseEntity<ResponseObject> validateAccessToken();
    ResponseEntity<ResponseObject> refreshAccessToken(HttpServletRequest request, TokenRefreshRequestForm tokenRefreshRequestForm);
    ResponseEntity<ResponseObject> logout(HttpServletRequest request, LogoutRequestForm logoutRequestForm);

}
