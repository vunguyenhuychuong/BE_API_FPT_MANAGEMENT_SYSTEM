package com.java8.tms.service.impl;

import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.user.dto.SignupUserDTO;
import com.java8.tms.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    private UserServiceImpl underTest;

    @BeforeEach
    void setUp() {
//        underTest = new UserServiceImpl(userRepository, passwordEncoder);
    }


    @Test
    void canFindUserByEmail() {
        //give
        String email = "quocsy2511@gmail.com";
        //when
        underTest.findUserByEmail(email);
        //then
        verify(userRepository).findByEmail(email);
    }

    @Test
    void existsByEmail() {
        //give
        String email = "quocsy2511@gmail.com";
        //when
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        assertThatThrownBy(() -> underTest.existsByEmail(email))
                .isInstanceOf(ExceptionHandler.class)
                .hasMessageContaining("email" + email + "doesnt exist");
//        verify(userRepository).existsByEmail(email);

    }

    @Test
    void save() {

        User user = User.builder()
                .email("quocsy2511@gmail.com")
                .password("123456")
                .expiredDate(Instant.now().plusSeconds(157680000))
                .createdDate(Instant.now())
                .fullname("nguyen quoc sy")
                .birthday(Date.valueOf("2000-10-10"))
                .gender(Gender.MALE)
                .status(UserStatus.OFF_CLASS)
                .build();
        underTest.save(user);
        verify(userRepository).save(user);

    }

    @Test
    @Disabled
    void canCreateNewAccount() {
        User user = User.builder()
                .email("quocsy2511@gmail.com")
                .password("123456")
                .expiredDate(Instant.now().plusSeconds(157680000))
                .createdDate(Instant.now())
                .fullname("nguyen quoc sy")
                .birthday(Date.valueOf("2000-10-10"))
                .gender(Gender.MALE)
                .status(UserStatus.OFF_CLASS)
                .build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        underTest.createNewAccount(user);
        verify(userRepository).save(user);
    }

//    @Test
//    void setUpUser() {
//        UUID uuid = UUID.fromString("4436720a-4def-11ed-bdc3-0242ac120002");
//        SignupUserDTO signupUserDTO = new SignupUserDTO(
//                "Nguyen Quoc Sy",
//                "quocsy2511@gmail.com",
//                "FEMALE",
//                null,
//                uuid
//
//        );
//        underTest.setUpUser(signupUserDTO, "1");
//
//    }

    @Test
    @Disabled
    void openOutLook() {
    }

    @Test
    @Disabled
    void generateSecurePassword() {
    }
}