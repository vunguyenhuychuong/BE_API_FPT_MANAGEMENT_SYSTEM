package com.java8.tms.role.service;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.payload.request.CreateRoleRequestForm;
import com.java8.tms.common.payload.request.UpdateRolePermissionsForm;
import org.springframework.http.ResponseEntity;

import javax.script.ScriptEngine;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleService {
    Optional<Role> findRoleByName(String name);
    Optional<Role> findRoleById(UUID id);
    ResponseEntity<ResponseObject> getAllRoles();
    public ResponseEntity<ResponseObject> updateRolePermissions(List<UpdateRolePermissionsForm> updateRolePermissionsForms);
    public ResponseEntity<ResponseObject> createRole(Set<CreateRoleRequestForm> createRoleRequestForms);
    public ResponseEntity<ResponseObject> getRolePermission();
    public ResponseEntity<ResponseObject> deleteRole(String roleId);
}
