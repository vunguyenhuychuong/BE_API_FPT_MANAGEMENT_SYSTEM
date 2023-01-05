package com.java8.tms.user.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.UserDTO;
import com.java8.tms.common.dto.UserPage;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.payload.request.*;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.user.dto.UpdateUserPasswordDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Optional<User> findUserByEmail(String username);

    Boolean existsByEmail(String email);

    User getUserByEmail(String email);

    User save(User user);

    // new code from user management
    Page<UserDTO> getFilterPaging(UserPage userPage, UserFilterForm userFilterForm);

    ResponseObject getAllUser(int pageNumber);

    Optional<User> findByEmail(String email);

    void changeRoleUser(ChangeRoleForm changeRoleForm);

    void deleteUser(UUID userId);

    boolean sortValidation(String sortDirection, String sortBy);

    void deActiveUser(UUID userId);

    UserDTO updateUser(UpdateUserForm userUpdate, UUID userId);

    UserPrinciple updateUserImage(MultipartFile image);
    public Optional<User> findUserByID(UUID id);

    User findById(UUID id);

    List<User> findAllById(List<UUID> ids);
    ResponseEntity<ResponseObject> getListKeyword(String keyword);

    UserDTO updateUserStatus(UpdateUserStatusForm updateUserStatusForm);

    UserDTO getUserLoginProfile();

    UserDTO updateUserProfile(UpdateUserProfileForm updateUserProfileForm);

    ResponseEntity<ResponseObject> updatePassword(UpdateUserPasswordDTO updateUserPasswordDTO);
}
