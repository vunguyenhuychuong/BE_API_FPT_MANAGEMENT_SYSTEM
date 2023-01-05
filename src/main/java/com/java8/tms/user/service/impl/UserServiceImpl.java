package com.java8.tms.user.service.impl;

import com.java8.tms.common.dto.Pagination;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.UserDTO;
import com.java8.tms.common.dto.UserPage;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;
import com.java8.tms.common.payload.request.*;
import com.java8.tms.common.repository.*;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.role.customException.RoleIdNotFoundException;
import com.java8.tms.role.service.RoleNotFoundException;
import com.java8.tms.role.service.impl.RoleServiceImpl;
import com.java8.tms.user.custom_exception.RoleAuthorizationAccessDeniedException;
import com.java8.tms.user.custom_exception.UserNotFoundException;
import com.java8.tms.user.dto.SignupUserDTO;
import com.java8.tms.user.dto.UpdateUserPasswordDTO;
import com.java8.tms.user.service.ImageService;
import com.java8.tms.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final FSURepository fsuRepository;
    private final UserFilterRepository userFilterRepository;
    private final AuthorityRepository AuthorityRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CacheManager cacheManager;


    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByID(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ValidationException("User is not existed"));
    }

    @Override
    public List<User> findAllById(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public ResponseEntity<ResponseObject> getListKeyword(String keyword) {
        String kw;
        // validate keyword entered by user
        if (keyword != null) {
            kw = keyword.replaceAll("\\s+", " ").stripLeading();
            if (kw.length() == 0)
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Fail to get keyword list, search value must not empty", null, null));
            Set<String> keywordList = userFilterRepository.getListKeyword(kw);
            if (keywordList.size() <= 1) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(HttpStatus.OK.toString(), "Keyword not found", null, keywordList)
                );
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(HttpStatus.OK.toString(), "Get search value list successfully", null,
                            keywordList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Fail to get search value list, search value is null", null,
                            null));
        }
    }

    /**
     * <p>
     * Save a new user to the database
     * </p>
     *
     * @param user
     * @author Minh Quan
     */
    public void createNewAccount(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
    }

    public void setUserPassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        user.setUpdatedDate(Instant.now());
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
    }

    /**
     * <p>
     * Setting up a new User
     * </p>
     *
     * @param signupUserDTO
     * @return User
     * @author Minh Quan
     */
    public User setUpUser(SignupUserDTO signupUserDTO, Role role) {
        // Generate random password
        String password = generateSecurePassword();
        // Setup User
        return User.builder()
                .email(signupUserDTO.getEmail())
                .password(password).createdDate(Instant.now())
                .fullname(signupUserDTO.getFullName()).birthday(signupUserDTO.getBirthday())
                .gender(Gender.valueOf(signupUserDTO.getGender().trim().toUpperCase())).status(UserStatus.ACTIVE)
                .role(role).build();
    }

    /**
     * <p>
     * Create a random String with 3 lowercases, 3 uppercases and 3 digits
     * </p>
     *
     * @return String
     * @author Minh Quan
     */
    public String generateSecurePassword() {

        // create character rule for lower case
        CharacterRule lowercase = new CharacterRule(EnglishCharacterData.LowerCase);
        // set number of lower case characters
        lowercase.setNumberOfCharacters(4);

        // create character rule for upper case
        CharacterRule uppercase = new CharacterRule(EnglishCharacterData.UpperCase);
        // set number of upper case characters
        uppercase.setNumberOfCharacters(4);

        // create character rule for digit
        CharacterRule digit = new CharacterRule(EnglishCharacterData.Digit);
        // set number of digits
        digit.setNumberOfCharacters(4);

        // create instance of the PasswordGenerator class
        PasswordGenerator passGen = new PasswordGenerator();

        return passGen.generatePassword(12, lowercase, uppercase, digit);

    }

    // new code from user management
    @Override
    public Page<UserDTO> getFilterPaging(UserPage userPage, UserFilterForm userFilterForm) {
        return userFilterRepository.UserFilterWithPaging(userFilterForm, userPage);
    }

    @Override
    public ResponseObject getAllUser(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("role").ascending());
        UserStatus status = UserStatus.DELETE;
        Page<User> userPage = userRepository.findAllByStatusIsNot(pageable, status);
        List<User> userList = userPage.getContent();
        ModelMapper modelMapper = new ModelMapper();
        List<UserDTO> userDTOList = modelMapper.map(userList, new TypeToken<List<UserDTO>>() {
        }.getType());
        Pagination pagination = new Pagination(pageNumber, 10, userPage.getTotalPages());

        ResponseObject responseObject;
        if (userList.size() > 0) {
            responseObject = new ResponseObject(HttpStatus.OK.toString(), "Get List User Successfully", pagination,
                    userDTOList);
        } else if (pageNumber >= userPage.getTotalPages() && userPage.getTotalPages() != 0) {
            responseObject = new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                    "Page number has less than total page", pagination, null);
        } else {
            responseObject = new ResponseObject(HttpStatus.NOT_FOUND.toString(), "List Is Empty", pagination, null);
        }
        return responseObject;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void changeRoleUser(ChangeRoleForm changeRoleForm) {
        UUID userId;
        UUID roleId;
        try {
            userId = UUID.fromString(changeRoleForm.getUserId());
            roleId = UUID.fromString(changeRoleForm.getRoleId());
        } catch (RuntimeException e) {
            throw new RuntimeException("UUID is wrong format");
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(changeRoleForm.getUserId(), "user id not found"));

        // check role update
        if (checkRoleAuthority(findUser))
            throw new RoleAuthorizationAccessDeniedException("Change role User", "You can not update this user");

        Role findRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(changeRoleForm.getRoleId(), "role id found"));

        // check role
        if (checkRoleUpdate(changeRoleForm.getRoleId()))
            throw new RoleAuthorizationAccessDeniedException("Change role User", "Can not update role with permission greater than you");

        if (isCurrentUser(findUser.getId()))
            throw new RoleAuthorizationAccessDeniedException("Delete User", "Can't delete your own account");

        findUser.setRole(findRole);
        userRepository.save(findUser);

        this.clearUserDetailsCache(findUser.getEmail());
    }

    @Override
    public void deleteUser(UUID userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString(), "user id not found"));
        if (checkRoleAuthority(findUser))
            throw new RoleAuthorizationAccessDeniedException("Delete User", "You can not update this user");
        if (isCurrentUser(findUser.getId()))
            throw new RoleAuthorizationAccessDeniedException("Delete User", "Can't delete your own account");
        findUser.setStatus(UserStatus.DELETE);
        userRepository.save(findUser);

        this.clearUserDetailsCache(findUser.getEmail());
    }

    @Override
    public void deActiveUser(UUID userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString(), "user id not found"));
        if (checkRoleAuthority(findUser))
            throw new RoleAuthorizationAccessDeniedException("DeActive User", "You can not update this user");
        if (isCurrentUser(findUser.getId()))
            throw new RoleAuthorizationAccessDeniedException("DeActive User", "Can't deActive your own account");
        findUser.setStatus(UserStatus.DEACTIVE);
        userRepository.save(findUser);

        this.clearUserDetailsCache(findUser.getEmail());
    }

    @Override
    public boolean sortValidation(String sortDirection, String sortBy) {
        boolean validation = false;
        if (sortDirection.trim().equalsIgnoreCase("ASC") || sortDirection.trim().equalsIgnoreCase("DESC")) {
            String[] arraysValue = {"id", "fullname", "email", "birthday", "gender", "level", "status", "role"};
            for (String type : arraysValue) {
                if (type.equals(sortBy)) {
                    validation = true;
                    break;
                }
            }
        }
        return validation;
    }

    public boolean userStatusValidation(String userStatus) {
        boolean check = false;
        Set<String> listStatus = UserStatus.getAllValueUserStatus();
        for (String status : listStatus) {
            if (status.equals(userStatus)) check = true;
        }
        return check;
    }

    @Override
    public UserDTO updateUser(UpdateUserForm userUpdate, UUID userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString(), "user id not found"));

        // check role permission
        if (checkRoleAuthority(findUser))
            throw new RoleAuthorizationAccessDeniedException("Update User", "You can not update this user");

        // update user
        UserDTO userResponse;
        findUser.setFullname(userUpdate.getFullname());
        findUser.setBirthday(userUpdate.getBirthday());
        findUser.setGender(Gender.valueOf(userUpdate.getGender().trim().toUpperCase()));
        findUser.setLevel(userUpdate.getLevel());
//      findUser.setStatus(UserStatus.valueOf(userUpdate.getStatus().trim().toUpperCase()));
        User user = userRepository.save(findUser);
        ModelMapper mapper = new ModelMapper();
        userResponse = mapper.map(user, UserDTO.class);

        this.clearUserDetailsCache(userResponse.getEmail());
        return userResponse;
    }


    @Override
    public UserDTO updateUserStatus(UpdateUserStatusForm userStatusForm) {

        UUID userId;
        try {
            userId = UUID.fromString(userStatusForm.getUserId());
        } catch (RuntimeException e) {
            throw new RuntimeException("UUID is wrong format");
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userStatusForm.getUserId(), "user id not found"));

        // check role permission
        if (checkRoleAuthority(findUser))
            throw new RoleAuthorizationAccessDeniedException("Update User", "You can not update this user");

        //check validate status user
        if (!userStatusValidation(userStatusForm.getStatusName()))
            throw new UserNotFoundException(userStatusForm.getStatusName(), "user status has not found");

        //check update owm status
        if (userStatusForm.getStatusName().equals("DELETE") || userStatusForm.getStatusName().equals("DEACTIVE")) {
            if (isCurrentUser(findUser.getId()))
                throw new RoleAuthorizationAccessDeniedException("Delete/DeActive User", "Can't delete or deActive your own account");
        }

        UserDTO userResponse;
        findUser.setStatus(UserStatus.valueOf(userStatusForm.getStatusName().trim().toUpperCase()));
        User user = userRepository.save(findUser);

        ModelMapper mapper = new ModelMapper();
        userResponse = mapper.map(user, UserDTO.class);

        this.clearUserDetailsCache(userResponse.getEmail());
        return userResponse;
    }

    @Override
    public UserDTO getUserLoginProfile() {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User findUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));

        ModelMapper mapper = new ModelMapper();
        return mapper.map(findUser, UserDTO.class);
    }

    @Override
    public UserDTO updateUserProfile(UpdateUserProfileForm updateUser) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User findUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));

        findUser.setFullname(updateUser.getFullname());
        findUser.setBirthday(updateUser.getBirthday());
        findUser.setGender(Gender.valueOf(updateUser.getGender().trim().toUpperCase()));

        User user = userRepository.save(findUser);
        ModelMapper mapper = new ModelMapper();
        UserDTO userResponse = mapper.map(user, UserDTO.class);

        this.clearUserDetailsCache(userResponse.getEmail());
        return userResponse;
    }

    @Override
    public UserPrinciple updateUserImage(MultipartFile image) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = userPrinciple.getId();
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId.toString(), "user id not found"));
        try {
            if (findUser.getAvatar() != null) {
                if (!imageService.deleteImage(findUser.getAvatar()))
                    throw new RuntimeException("old image user delete error");
            }
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
        }
        UserPrinciple userResponse = new UserPrinciple();
        String imagePath = imageService.upload(image);

        if (imagePath != null) {
            findUser.setAvatar(imagePath);
            User user = userRepository.save(findUser);

            userResponse = UserPrinciple.build(user);
            this.clearUserDetailsCache(userResponse.getEmail());
        }

        return userResponse;
    }

    private void clearUserDetailsCache(String userEmail) {
        boolean result = cacheManager.getCache("userDetails").evictIfPresent(userEmail);
        if (result) {
            LOGGER.info("Clear account " + userEmail + " from cache");
        } else {
            LOGGER.error("Fail clear account " + userEmail + " from cache");
        }
    }

    public String getCurrentPermissionWithUser() {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        for (GrantedAuthority grantedAuthority : userPrinciple.getAuthorities()) {
            String str = grantedAuthority.toString();
            if (str.equalsIgnoreCase("FULL_ACCESS_USER"))
                return "FULL_ACCESS_USER";
            if (str.equalsIgnoreCase("MODIFY_USER"))
                return "MODIFY_USER";
        }
        return "CREATE_USER";
    }

    public boolean isCurrentUser(UUID userUpdateId) {
        boolean check = false;
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrinciple.getId().equals(userUpdateId)) {
            check = true;
        }

        return check;
    }

    public boolean checkRoleAuthority(User findUser) {
        boolean check = false;
        String currentUserRole = getCurrentPermissionWithUser();
        String updateUserRole = AuthorityRepository.finAllByRoleIdAndResource2(findUser.getRole().getId().toString(),
                "USER");
        if (currentUserRole.equalsIgnoreCase("CREATE_USER")) {
            if (updateUserRole.equalsIgnoreCase("CREATE") || updateUserRole.equalsIgnoreCase("MODIFY")
                    || updateUserRole.equalsIgnoreCase("VIEW") || updateUserRole.equalsIgnoreCase("NO_ACCESS"))
                check = true;
        } else if (currentUserRole.equalsIgnoreCase("MODIFY_USER")) {
            if (updateUserRole.equalsIgnoreCase("MODIFY") || updateUserRole.equalsIgnoreCase("VIEW") || updateUserRole.equalsIgnoreCase("NO_ACCESS"))
                check = true;
        } else {
            check = true;
        }
        return !check;
    }

    public boolean checkRoleUpdate(String roleId){
        boolean check = false;
        String currentUserRole = getCurrentPermissionWithUser();
        String updateRolePermission = AuthorityRepository.finAllByRoleIdAndResource2(roleId,"USER");
        if (currentUserRole.equalsIgnoreCase("CREATE_USER")) {
            if (updateRolePermission.equalsIgnoreCase("CREATE") || updateRolePermission.equalsIgnoreCase("MODIFY")
                    || updateRolePermission.equalsIgnoreCase("VIEW") || updateRolePermission.equalsIgnoreCase("NO_ACCESS"))
                check = true;
        } else if (currentUserRole.equalsIgnoreCase("MODIFY_USER")) {
            if (updateRolePermission.equalsIgnoreCase("MODIFY") || updateRolePermission.equalsIgnoreCase("VIEW") || updateRolePermission.equalsIgnoreCase("NO_ACCESS"))
                check = true;
        } else {
            check = true;
        }
        return !check;
    }

    /**
     * <p>
     * Generate a random 3 digits and 3 uppercase String
     * </p>
     *
     * @return String
     * @author Minh Quan
     */
    public String randomGenerateOTP() {
        // create character rule for upper case
        CharacterRule uppercase = new CharacterRule(EnglishCharacterData.UpperCase);
        // set number of upper case characters
        uppercase.setNumberOfCharacters(3);

        // create character rule for digit
        CharacterRule digit = new CharacterRule(EnglishCharacterData.Digit);
        // set number of digits
        digit.setNumberOfCharacters(3);

        // create instance of the PasswordGenerator class
        return new PasswordGenerator().generatePassword(6, uppercase, digit);
    }

    @Override
    public ResponseEntity<ResponseObject> updatePassword(UpdateUserPasswordDTO updateUserPasswordDTO) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOGGER.info("Start update user password");
        if (!passwordEncoder.matches(updateUserPasswordDTO.getCurrentPassword(), userPrinciple.getPassword())) {
            LOGGER.error("Current password not match for {}", userPrinciple.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "The current password does not match your previous password", null, null));
        }
        if (!updateUserPasswordDTO.getNewPassword().equals(updateUserPasswordDTO.getConfirmNewPassword())) {
            LOGGER.error("Both new password and confirm password do not match for {}", userPrinciple.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "Both new password and confirm password do not match", null, null));
        }
        this.setUserPassword(userPrinciple.getEmail(), updateUserPasswordDTO.getNewPassword());
        LOGGER.info("Update password success for {}", userPrinciple.getEmail());
        this.clearUserDetailsCache(userPrinciple.getEmail());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Update password success!", null, null));
    }
}
