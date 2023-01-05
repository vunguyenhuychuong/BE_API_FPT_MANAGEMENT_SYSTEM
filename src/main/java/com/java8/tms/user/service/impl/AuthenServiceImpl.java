package com.java8.tms.user.service.impl;


import com.java8.tms.common.dto.AuthorityDTO;
import com.java8.tms.common.dto.AuthorityWithoutIdDTO;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.UserPrincipleDTO;
import com.java8.tms.common.entity.BlackAccessToken;
import com.java8.tms.common.entity.RefreshToken;
import com.java8.tms.common.payload.request.LogoutRequestForm;
import com.java8.tms.common.payload.request.SignInForm;
import com.java8.tms.common.payload.request.TokenRefreshRequestForm;
import com.java8.tms.common.payload.response.JwtResponse;
import com.java8.tms.common.payload.response.TokenRefreshResponse;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.common.security.jwt.*;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.common.utils.EmailValidation;
import com.java8.tms.role.service.impl.RoleServiceImpl;
import com.java8.tms.user.service.AuthenService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Set;

@Service
public class AuthenServiceImpl implements AuthenService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenServiceImpl.class);

    private final String companyEmail = "FA.HCM@fsoft.com.vn";
    @Autowired
    private final ModelMapper mapper;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenProvider refreshTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BlackAccessTokenServiceImp blackAccessTokenServiceImp;
    @Autowired
    private UserServiceImpl userServiceImpl;
    
    public AuthenServiceImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public ResponseEntity<ResponseObject> validateLoginForm(SignInForm signInForm) {

        if ((signInForm.getEmail().isEmpty() || signInForm.getEmail().isBlank()) && (signInForm.getPassword().isEmpty() || signInForm.getPassword().isBlank())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Empty input field!", null, null));
        } else if (signInForm.getEmail().isEmpty() || signInForm.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Empty email!", null, null));
        } else if (signInForm.getPassword().isEmpty() || signInForm.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Empty password!", null, null));
        }
        if (!new EmailValidation().validateEmail(signInForm.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid email format!", null, null));
        }

        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> login(SignInForm signInForm) {
        ResponseEntity<ResponseObject> responseEntity = this.validateLoginForm(signInForm);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInForm.getEmail(), signInForm.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            String accessToken = jwtProvider.createToken(userPrinciple);
            String refreshToken = refreshTokenProvider.createRefreshToken(signInForm.getEmail()).getToken();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Login success!", null, new JwtResponse(accessToken, refreshToken)));
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Account has been locked. Please contact " + companyEmail + " for more information", null, null));
            }
            if(e instanceof AccountExpiredException){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "The account has expired. Please contact " + companyEmail + " for more information", null, null));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Invalid email or password. Please try again.", null, null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> validateAccessToken() {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipleDTO userPrincipleDTO = mapper.map(userPrinciple, UserPrincipleDTO.class);
        userPrincipleDTO.setAuthorities(mapper.map(userPrinciple.getDefaultAuthorities(), new TypeToken<Set<AuthorityWithoutIdDTO>>() {}.getType()));;
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Validate access token success!", null, userPrincipleDTO));
    }

    @Override
    public ResponseEntity<ResponseObject> refreshAccessToken(HttpServletRequest request, TokenRefreshRequestForm tokenRefreshRequestForm) {
        String accessToken = jwtProvider.getJwt(request);
        try {
            jwtProvider.validateTokenThrowException(accessToken);
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                return refreshTokenProvider.findByToken(DigestUtils.sha3_256Hex(tokenRefreshRequestForm.getRefreshToken()))
                        .map(refreshTokenProvider::verifyExpiration)
                        .map(RefreshToken::getUser)
                        .map(user -> {
                            UserPrinciple userPrinciple = UserPrinciple.build(user);
                            String newAccessToken = jwtProvider.createToken(userPrinciple);
                            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Refresh token success!", null, new TokenRefreshResponse(newAccessToken, tokenRefreshRequestForm.getRefreshToken())));
                        })
                        .orElseThrow(() -> new RefreshTokenException("Refresh token is not in database!"));
            }
            throw new JwtTokenException("Error -> Unauthorized");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Cannot create new access token -> Old access token is not expired", null, null));
    }

    @Override
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, LogoutRequestForm logoutRequestForm) {
        //delete refresh token
        refreshTokenProvider.deleteByToken(logoutRequestForm.getRefreshToken());
        //set access token into black list to prevent reused.
        String accessToken = jwtProvider.getJwt(request);
        Instant expiredTime = jwtProvider.getAccessTokenExpiredTime(accessToken).toInstant();
        BlackAccessToken blackAccessToken = BlackAccessToken.builder()
                .accessToken(DigestUtils.sha3_256Hex(accessToken))
                .expiryDate(expiredTime)
                .build();
        blackAccessTokenServiceImp.save(blackAccessToken);

        //For cache
        this.clearRefreshTokenCache(logoutRequestForm.getRefreshToken());
        this.clearUserDetailsCache(jwtProvider.getEmailFromToken(accessToken));

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Logout success!", null, new TokenRefreshResponse(null, null)));
    }
    


    private void clearRefreshTokenCache(String refreshToken) {
        boolean result = cacheManager.getCache("refreshToken").evictIfPresent(refreshToken);
        if (result) {
            logger.info("Clear refresh token " + refreshToken + " from cache");
        } else {
            logger.error("Fail clear refresh token " + refreshToken + " from cache");
        }
    }

    private void clearUserDetailsCache(String userEmail) {
        boolean result = cacheManager.getCache("userDetails").evictIfPresent(userEmail);
        if (result) {
            logger.info("Clear account " + userEmail + " from cache");
        } else {
            logger.error("Fail clear account " + userEmail + " from cache");
        }
    }

}
