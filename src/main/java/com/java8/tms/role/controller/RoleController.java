package com.java8.tms.role.controller;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.payload.request.CreateRoleRequestForm;
import com.java8.tms.common.payload.request.UpdateRolePermissionsForm;
import com.java8.tms.role.service.impl.RoleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class RoleController {
    @Autowired
    private RoleServiceImpl roleService;

    @PutMapping("/roles")
    @PreAuthorize("hasAuthority('FULL_ACCESS_USER')")
    @Operation(summary = "For updating role permission")
    public ResponseEntity<ResponseObject> updateRolePermissions(@Valid @RequestBody List<UpdateRolePermissionsForm> updateRolePermissionsForms) {
        if (updateRolePermissionsForms.isEmpty()) {
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input updateRolePermissionsForms", null, null), HttpStatus.BAD_REQUEST);
        }
        for (UpdateRolePermissionsForm updateRolePermissionsForm: updateRolePermissionsForms) {
            if (updateRolePermissionsForm.getRoleId() == null || updateRolePermissionsForm.getRoleId().toString().isEmpty() || updateRolePermissionsForm.getRoleId().toString().isBlank()){
                return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input role id", null, null), HttpStatus.BAD_REQUEST);
            }
            for (UUID authorityId: updateRolePermissionsForm.getAuthoritiesId()) {
                if(authorityId == null || authorityId.toString().isEmpty() || authorityId.toString().isBlank()){
                    return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input authority id", null, null), HttpStatus.BAD_REQUEST);
                }
            }
        }
        return roleService.updateRolePermissions(updateRolePermissionsForms);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    @Operation(summary = "For getting all role")
    public ResponseEntity<ResponseObject> getRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/roles/permissions")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    @Operation(summary = "For getting all current role permission")
    public ResponseEntity<ResponseObject> getRolePermission() {
        return roleService.getRolePermission();
    }

    @PostMapping("/roles/create")
    @PreAuthorize("hasAuthority('FULL_ACCESS_USER')")
    @Operation(summary = "For creating new role")
    public ResponseEntity<ResponseObject> createRole(@RequestBody Set<@Valid CreateRoleRequestForm> createRoleRequestForms) {
        if (createRoleRequestForms.isEmpty()) {
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input create role request forms", null, null), HttpStatus.BAD_REQUEST);
        }
        for (CreateRoleRequestForm createRoleRequestForm: createRoleRequestForms) {
            if (createRoleRequestForm.getRoleName() == null || createRoleRequestForm.getRoleName().isEmpty() || createRoleRequestForm.getRoleName().isBlank()){
                return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input role name", null, null), HttpStatus.BAD_REQUEST);
            }
            for (UUID authorityId: createRoleRequestForm.getAuthoritiesId()) {
                if(authorityId == null || authorityId.toString().isEmpty() || authorityId.toString().isBlank()){
                    return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input authority id", null, null), HttpStatus.BAD_REQUEST);
                }
            }
        }
        return roleService.createRole(createRoleRequestForms);
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('FULL_ACCESS_USER')")
    @Operation(summary = "For delete role")
    public ResponseEntity<ResponseObject> deleteRole(@Parameter(description = "enter role id", required = true, example = "1b38f891-df7e-47db-a1ff-5889a3e9422e")  @PathVariable(name = "id") String roleId){
        return roleService.deleteRole(roleId);
    }
}
