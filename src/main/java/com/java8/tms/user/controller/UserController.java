package com.java8.tms.user.controller;

import com.java8.tms.common.dto.Pagination;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.UserDTO;
import com.java8.tms.common.dto.UserPage;
import com.java8.tms.common.meta.UserStatus;
import com.java8.tms.common.payload.request.ChangeRoleForm;
import com.java8.tms.common.payload.request.UpdateUserForm;
import com.java8.tms.common.payload.request.UpdateUserStatusForm;
import com.java8.tms.common.payload.request.UserFilterForm;
import com.java8.tms.user.dto.SignupUserDTO;
import com.java8.tms.user.service.ImportUserService;
import com.java8.tms.user.service.UserService;
import com.java8.tms.user.service.impl.SignupServiceImpl;
import com.sun.jdi.InternalException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ImportUserService importUserService;
    @Autowired
    private SignupServiceImpl signupServiceImpl;

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @Operation(summary = "for get list keyword to search")
    @GetMapping("/suggest")
    public ResponseEntity<ResponseObject> getListKeyword(
            @Parameter(description = "Keyword to search (EX: Nguyen, Nguyen Trong Nguyen Vu)")
            @RequestParam(name = "key") @Size(max = 50) String keyword
    ) {
        return userService.getListKeyword(keyword);
    }


    @PreAuthorize("hasAuthority('VIEW_USER')")
    @Operation(summary = "for get all user page filter and paging")
    @GetMapping("")
    public ResponseEntity<ResponseObject> filterWithPaging(
            @Parameter(description = "Filter with sort type (EX: asc,desc)") @RequestParam(name = "sortType", required = false) String sortDirection,

            @Parameter(description = "Sort by (EX: id,fullname,email,birthday,gender,level,status,role)") @RequestParam(name = "sortBy", required = false) String sortBy,

            @Parameter(description = "Search user by name (EX: le,quan...)") @RequestParam(name = "searchValue", required = false) Set<String> searchName,

            @Parameter(description = "Filter with gender (EX: male,female)") @RequestParam(name = "gender", required = false) String gender,

            @Parameter(description = "Filter with role type (EX: Trainer,Student,Class Admin,Super Admin...)") @RequestParam(name = "roleType", required = false) Set<String> roleType,

            @Parameter(description = "Filter with status (EX: active,deactive,off_class,in_class,on_boarding)") @RequestParam(name = "status", required = false) Set<String> status,

            @Parameter(description = "Filter with from date (EX: 1990-01-01)") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "fromDate", required = false) Date fromDate,

            @Parameter(description = "Filter with to date (EX:2005-01-01)") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "toDate", required = false) Date toDate,

            @Parameter(description = "Page Number (1...)", example = "1") @RequestParam("page") @Min(value = 1, message = "Page number must be greater than or equal to 1") int pageNumber,

            @Parameter(description = "Page size (1...)", example = "1") @RequestParam("size") @Min(value = 1, message = "Page size must be greater than or equal to 1") int pageSize
    ) throws Exception {

        // build user form filter
        UserFilterForm userFilterForm = UserFilterForm.builder().searchValue(searchName).gender(gender)
                .typeRole(roleType).status(status).fromBirthday(fromDate).toBirthday(toDate).build();

        // set paging
        UserPage userPage = new UserPage();
        userPage.setPageNumber(pageNumber - 1);
        userPage.setPageSize(pageSize);

        // set sorting
        if (sortDirection != null || sortBy != null) {
            boolean validation = userService.sortValidation(sortDirection, sortBy);
            if (validation) {
                userPage.setSortBy(sortBy);
                userPage.setSortDirection(sortDirection.trim().toUpperCase());
            } else {
                log.error("sort direction or sort by has not validate");
                throw new RuntimeException("sort direction or sort by has not validate");
            }
        }

        Page<UserDTO> listUser = userService.getFilterPaging(userPage, userFilterForm);
        Pagination pagination = new Pagination(pageNumber, userPage.getPageSize(), listUser.getTotalPages());
        List<UserDTO> userDTOList = listUser.getContent();

        HttpStatus statusResponse;
        // set response object
        ResponseObject responseObject;
        if (userDTOList.size() > 0) {
            responseObject = new ResponseObject(HttpStatus.OK.toString(), "Get List User Successfully", pagination,
                    userDTOList);
            statusResponse = HttpStatus.OK;
        } else if (pageNumber >= listUser.getTotalPages() && listUser.getTotalPages() != 0) {
            throw new Exception("Page number has less than total page");
        } else {
            responseObject = new ResponseObject(HttpStatus.OK.toString(), "User Not Found", null, userDTOList);
            statusResponse = HttpStatus.OK;
        }

        return ResponseEntity.status(statusResponse).body(responseObject);
    }

    @PreAuthorize("hasAuthority('MODIFY_USER')")
    @Operation(summary = "for change role user")
    @PutMapping(value = "/change-role")
    public ResponseEntity<ResponseObject> changeRoleUser(@Valid @RequestBody ChangeRoleForm changeRoleForm
    ) {

        ResponseObject responseObject;
        // check update role
        userService.changeRoleUser(changeRoleForm);
        String pattern = "User id {0} has update with role id {1} successfully";
        responseObject = new ResponseObject(HttpStatus.OK.toString(),
                MessageFormat.format(pattern, changeRoleForm.getUserId(), changeRoleForm.getRoleId()), null,
                null);

        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    @PreAuthorize("hasAuthority('MODIFY_USER')")
    @Operation(summary = "for update user by id")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateUser(
            @Parameter(description = "enter user id", required = true, example = "87ad5bc2-3bde-439a-97d0-63c4e44d19d9") @NotNull @PathVariable(name = "id") UUID userId,
            @Valid @RequestBody UpdateUserForm userUpdate) {
        ResponseObject responseObject;
        UserDTO userDTO = userService.updateUser(userUpdate, userId);
        HttpStatus responseStatus;
        if (userDTO != null) {
            responseObject = new ResponseObject(HttpStatus.OK.toString(), "User update successfully!", null, userDTO);
            responseStatus = HttpStatus.OK;
        } else {
            throw new InternalException("User Update Fail");
        }
        return ResponseEntity.status(responseStatus).body(responseObject);
    }


    @PreAuthorize("hasAuthority('MODIFY_USER')")
    @Operation(summary = "for delete user by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteUser(
            @Parameter(description = "enter user id to delete ", example = "87ad5bc2-3bde-439a-97d0-63c4e44d19d9", required = true) @PathVariable(name = "id") @NotBlank UUID userId) {
        userService.deleteUser(userId);
        String pattern = "User id {0} has delete successfully";
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.toString(),
                MessageFormat.format(pattern, userId), null, null);

        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    @PreAuthorize("hasAuthority('MODIFY_USER')")
    @Operation(summary = "for de-active user by user id")
    @PutMapping("/de-active/{id}")
    public ResponseEntity<ResponseObject> deActiveUser(
            @Parameter(description = "enter user id to deActive", example = "87ad5bc2-3bde-439a-97d0-63c4e44d19d9", required = true) @PathVariable(name = "id") @NotBlank UUID userId) {

        userService.deActiveUser(userId);
        String pattern = "User id {0} has deActive successfully";
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.toString(),
                MessageFormat.format(pattern, userId), null, null);

        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }


    @PostMapping(value = "/create")
    @Operation(summary = "for create new account")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<ResponseObject> createNewAccount(@Valid @RequestBody SignupUserDTO signupUserDTO) {
        return signupServiceImpl.createNewAccount(signupUserDTO);
    }

    @GetMapping("/status")
    @Operation(summary = "for get all status user")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    public ResponseEntity<ResponseObject> getAllStatusUser() {
        Set<String> listStatus = UserStatus.getAllValueUserStatus();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "get user status successfully", null, listStatus));
    }

    @PutMapping("/status")
    @Operation(summary = "for update user status")
    @PreAuthorize("hasAuthority('MODIFY_USER')")
    public ResponseEntity<ResponseObject> changeUserStatus(@Valid @RequestBody UpdateUserStatusForm updateUserStatusForm
    ) {
        UserDTO userResponse = userService.updateUserStatus(updateUserStatusForm);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "update user status successfully", null, userResponse));
    }
}