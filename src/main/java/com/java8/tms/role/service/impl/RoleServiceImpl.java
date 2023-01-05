package com.java8.tms.role.service.impl;


import com.java8.tms.authority.customException.AuthorityNotFoundException;
import com.java8.tms.common.dto.*;
import com.java8.tms.common.entity.Authority;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.payload.request.CreateRoleRequestForm;
import com.java8.tms.common.payload.request.UpdateRolePermissionsForm;
import com.java8.tms.common.repository.AuthorityRepository;
import com.java8.tms.common.repository.RoleRepository;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.role.customException.RoleIdNotFoundException;
import com.java8.tms.role.customException.RoleNameNotFoundException;
import com.java8.tms.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private final ModelMapper mapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    public RoleServiceImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public Optional<Role> findRoleById(UUID id) {
        return roleRepository.findRoleById(id);
    }


    @Override
    public ResponseEntity<ResponseObject> getAllRoles() {
        Set<RoleWithoutAuthorDTO> roleDTOS = mapper.map(new HashSet<>(roleRepository.findAll()), new TypeToken<Set<RoleWithoutAuthorDTO>>() {
        }.getType());
        return new ResponseEntity<>(
                new ResponseObject(
                        HttpStatus.OK.toString()
                        , "Get all role success!"
                        , null
                        , roleDTOS)
                , HttpStatus.OK);
    }

    private ResponseEntity<ResponseObject> validateUpdateRolePermissionsForms(List<UpdateRolePermissionsForm> updateRolePermissionsForms, UUID superAdminId) {
        if (updateRolePermissionsForms.size() == 1
                && updateRolePermissionsForms
                .stream()
                .anyMatch(updateRolePermissionsForm -> superAdminId.equals(updateRolePermissionsForm.getRoleId()))) {
            return new ResponseEntity<ResponseObject>(
                    new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString()
                            , "Update role's permissions fail! -> Error: Update Super Admin permission is not accepted!"
                            , null
                            , null
                    )
                    , HttpStatus.NOT_ACCEPTABLE);
        }

        for (UpdateRolePermissionsForm updateRolePermissionsForm : updateRolePermissionsForms) {
            ResponseObject checkDuplicateAuthoritiesIdResponseObject = this.checkDuplicateAuthoritiesId(updateRolePermissionsForm.getAuthoritiesId());
            if (checkDuplicateAuthoritiesIdResponseObject != null) {
                return new ResponseEntity<>(
                        checkDuplicateAuthoritiesIdResponseObject
                        , HttpStatus.NOT_ACCEPTABLE);
            }

            ResponseObject checkAuthoritiesIdListSizeResponseObject = this.checkAuthoritiesIdListSize(updateRolePermissionsForm.getAuthoritiesId());
            if (checkAuthoritiesIdListSizeResponseObject != null) {
                return new ResponseEntity<>(
                        checkAuthoritiesIdListSizeResponseObject
                        , HttpStatus.NOT_ACCEPTABLE);
            }

//        List<UpdateRolePermissionsForm> invalidAuthoritiesSizeList = updateRolePermissionsForms
//                .stream()
//                .filter(updateRolePermissionsForm -> updateRolePermissionsForm.getAuthoritiesId().size() < 5)
//                .collect(Collectors.toList());
//
//        if (invalidAuthoritiesSizeList.size() != 0) {
//            return new ResponseEntity<ResponseObject>(
//                    new ResponseObject(
//                            HttpStatus.NOT_ACCEPTABLE.toString()
//                            , "Update role's permissions fail! -> Error: Roles id " + invalidAuthoritiesSizeList.stream().map(UpdateRolePermissionsForm::getRoleId).collect(Collectors.toList()) + " do not have enough authorities"
//                            , null
//                            , null)
//                    , HttpStatus.NOT_ACCEPTABLE);
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateRolePermissions(List<UpdateRolePermissionsForm> updateRolePermissionsForms) {
        try {
            UUID superAdminId = roleRepository.findRoleByName("SUPER_ADMIN")
                    .orElseThrow(() -> new RoleNameNotFoundException("SUPER_ADMIN", "NOT FOUND!")).getId();

            ResponseEntity<ResponseObject> responseEntityValidateForm = validateUpdateRolePermissionsForms(updateRolePermissionsForms, superAdminId);
            if (responseEntityValidateForm != null) {
                return responseEntityValidateForm;
            }

            List<RoleDTO> roleDTOS = new ArrayList<>();
            updateRolePermissionsForms.stream()
                    .filter(updateRolePermissionsForm -> !superAdminId.equals(updateRolePermissionsForm.getRoleId()))
                    .filter(updateRolePermissionsForm -> updateRolePermissionsForm.getAuthoritiesId().size() == 5)
                    .collect(Collectors.toList())
                    .forEach(updateRolePermissionsForm -> {
                        Set<Authority> authorities = new HashSet<Authority>();

                        updateRolePermissionsForm.getAuthoritiesId().forEach(id -> {
                            Authority authority = authorityRepository.findOneById(id).orElseThrow(() -> new AuthorityNotFoundException(id, "NOT FOUND"));
                            authorities.add(authority);
                        });

                        Role role = roleRepository.findRoleById(updateRolePermissionsForm.getRoleId()).orElseThrow(() -> new RoleIdNotFoundException(updateRolePermissionsForm.getRoleId(), "NOT FOUND"));
                        //                role.setAuthorities(mapper.map(authoritiesDTO, new TypeToken<Set<Authority>>() {}.getType()));
                        role.setAuthorities(authorities);
                        roleRepository.save(role);
                        roleDTOS.add(mapper.map(role, RoleDTO.class));
                    });

            return new ResponseEntity<>(
                    new ResponseObject(
                            HttpStatus.CREATED.toString()
                            , "Update role's permissions success!"
                            , null
                            , roleDTOS)
                    , HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    new ResponseObject(
                            HttpStatus.NOT_ACCEPTABLE.toString()
                            , "Update role's permissions fail! -> Error: " + e.getMessage()
                            , null
                            , null
                    )
                    , HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createRole(Set<CreateRoleRequestForm> createRoleRequestForms) {
        try {
            //validation
            ResponseEntity<ResponseObject> validateCreateRoleFormResponse = this.validateCreateRoleForm(createRoleRequestForms);
            if (validateCreateRoleFormResponse != null) {
                return validateCreateRoleFormResponse;
            }

            List<RoleDTO> roleDTOS = new ArrayList<>();
            createRoleRequestForms
                    .forEach(createRoleRequestForm -> {

                        Set<Authority> authorities = new HashSet<Authority>();

                        createRoleRequestForm.getAuthoritiesId().forEach(id -> {
                            Authority authority = authorityRepository.findOneById(id).orElseThrow(() -> new AuthorityNotFoundException(id, "NOT FOUND"));
                            authorities.add(authority);
                        });

                        Role role = Role.builder()
                                .name(createRoleRequestForm.getRoleName())
                                .authorities(authorities)
                                .build();
                        roleRepository.save(role);

                        roleDTOS.add(mapper.map(role, RoleDTO.class));

                    });

            return new ResponseEntity<>(
                    new ResponseObject(
                            HttpStatus.CREATED.toString()
                            , "Create role's permissions success!"
                            , null
                            , roleDTOS)
                    , HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    new ResponseObject(
                            HttpStatus.NOT_ACCEPTABLE.toString()
                            , "Update role's permissions fail! -> Error: " + e.getMessage()
                            , null
                            , null
                    )
                    , HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private ResponseEntity<ResponseObject> validateCreateRoleForm(Set<CreateRoleRequestForm> createRoleRequestForms) {
        ResponseObject checkDuplicateRoleNameResponseObject = this.checkDuplicateRoleName(createRoleRequestForms.stream().map(CreateRoleRequestForm::getRoleName).collect(Collectors.toList()));
        if (checkDuplicateRoleNameResponseObject != null) {
            return new ResponseEntity<ResponseObject>(
                    checkDuplicateRoleNameResponseObject
                    , HttpStatus.NOT_ACCEPTABLE);
        }

        for (CreateRoleRequestForm createRoleRequestForm : createRoleRequestForms) {

            ResponseObject checkExitsRoleNameResponseObject = this.checkExistRoleName(createRoleRequestForm.getRoleName());
            if (checkExitsRoleNameResponseObject != null) {
                return new ResponseEntity<ResponseObject>(
                        checkExitsRoleNameResponseObject
                        , HttpStatus.NOT_ACCEPTABLE);
            }

            ResponseObject checkDuplicateAuthoritiesIdResponseObject = this.checkDuplicateAuthoritiesId(createRoleRequestForm.getAuthoritiesId());
            if (checkDuplicateAuthoritiesIdResponseObject != null) {
                return new ResponseEntity<ResponseObject>(
                        checkDuplicateAuthoritiesIdResponseObject
                        , HttpStatus.NOT_ACCEPTABLE);
            }

            ResponseObject checkAuthoritiesIdListSizeResponseObject = this.checkAuthoritiesIdListSize(createRoleRequestForm.getAuthoritiesId());
            if (checkAuthoritiesIdListSizeResponseObject != null) {
                return new ResponseEntity<ResponseObject>(
                        checkAuthoritiesIdListSizeResponseObject
                        , HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return null;
    }

//    private ResponseObject checkDuplicateAuthoritiesResource(List<UUID> authoritiesId) {
//        Set<String> duplicateResourceList = new HashSet<>();
//
//        Authority authorityCurrent = new Authority();
//        Authority authorityNext = new Authority();
//        for (int i = 0; i < authoritiesId.size(); i++) {
//            for (int j = i + 1; j < authoritiesId.size(); j++) {
//                UUID authorityIdCurrent = authoritiesId.get(i);
//                authorityCurrent = authorityRepository.findOneById(authorityIdCurrent).orElseThrow(() -> new AuthorityNotFoundException(authorityIdCurrent, "Not found"));
//
//                UUID authorityIdNext = authoritiesId.get(j);
//                authorityNext = authorityRepository.findOneById(authoritiesId.get(j)).orElseThrow(() -> new AuthorityNotFoundException(authorityIdNext, "Not found"));
//                if (authorityCurrent.getResource().equals(authorityNext.getResource())) {
//                    duplicateResourceList.add(authorityCurrent.getResource());
//                }
//            }
//        }
//
//        if (!duplicateResourceList.isEmpty()) {
//            return new ResponseObject(
//                    HttpStatus.NOT_ACCEPTABLE.toString()
//                    , "Update role's permissions fail! -> Error: duplicate authorities resource : " + duplicateResourceList
//                    , null
//                    , null
//            );
//        }
//        return null;
//    }

    private ResponseObject checkDuplicateAuthoritiesId(List<UUID> authoritiesId) {
        Set<UUID> duplicateIdList = new HashSet<>();
        Set<String> duplicateResourceList = new HashSet<>();

        Authority authorityCurrent = new Authority();
        Authority authorityNext = new Authority();
        for (int i = 0; i < authoritiesId.size(); i++) {
            UUID authorityIdCurrent = authoritiesId.get(i);
            authorityCurrent = authorityRepository.findOneById(authorityIdCurrent).orElseThrow(() -> new AuthorityNotFoundException(authorityIdCurrent, "Not found"));
            for (int j = i + 1; j < authoritiesId.size(); j++) {
                if (authoritiesId.get(i).compareTo(authoritiesId.get(j)) == 0) {
                    duplicateIdList.add(authoritiesId.get(i));
                }
                else {
                    UUID authorityIdNext = authoritiesId.get(j);
                    authorityNext = authorityRepository.findOneById(authoritiesId.get(j)).orElseThrow(() -> new AuthorityNotFoundException(authorityIdNext, "Not found"));
                    if (authorityCurrent.getResource().equals(authorityNext.getResource())) {
                        duplicateResourceList.add(authorityCurrent.getResource());
                    }
                }

            }
        }

        if (!duplicateIdList.isEmpty()) {
            return new ResponseObject(
                    HttpStatus.NOT_ACCEPTABLE.toString()
                    , "Update role's permissions fail! -> Error: duplicate authorities id: " + duplicateIdList
                    , null
                    , null
            );
        }
        if (!duplicateResourceList.isEmpty()) {
            return new ResponseObject(
                    HttpStatus.NOT_ACCEPTABLE.toString()
                    , "Update role's permissions fail! -> Error: duplicate authorities resource : " + duplicateResourceList
                    , null
                    , null
            );
        }
        return null;
    }

    private ResponseObject checkDuplicateRoleName(List<String> roleNameList) {
        Set<String> duplicateRoleNameList = new HashSet<>();

        for (int i = 0; i < roleNameList.size(); i++) {
            for (int j = i + 1; j < roleNameList.size(); j++) {
                if (roleNameList.get(i).compareTo(roleNameList.get(j)) == 0) {
                    duplicateRoleNameList.add(roleNameList.get(i));
                }
            }
        }

        if (!duplicateRoleNameList.isEmpty()) {
            return new ResponseObject(
                    HttpStatus.NOT_ACCEPTABLE.toString()
                    , "Update role's permissions fail! -> Error: duplicate role name: " + duplicateRoleNameList
                    , null
                    , null
            );
        }
        return null;
    }

    private ResponseObject checkExistRoleName(String roleName) {
        if (roleRepository.findRoleByName(roleName).isPresent()) {
            return new ResponseObject(
                    HttpStatus.NOT_ACCEPTABLE.toString()
                    , "Update role's permissions fail! -> Error: role name " + roleName + " is used"
                    , null
                    , null
            );
        }
        return null;
    }

    private ResponseObject checkAuthoritiesIdListSize(List<UUID> authoritiesId) {
        if (authoritiesId.size() != 5) {
            return new ResponseObject(
                    HttpStatus.NOT_ACCEPTABLE.toString()
                    , "Update role's permissions fail! -> Error: 5 authorities id is required!"
                    , null
                    , null
            );
        }
        return null;

    }

    @Override
    public ResponseEntity<ResponseObject> getRolePermission() {

        List<Role> roles = roleRepository.findAllByOrderById();
        List<RolePermissionDTO> responseRolePermissionList = new ArrayList<>();

        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrinciple.getDefaultAuthorities().stream().filter(authority -> authority.appendAuthority().equals("FULL_ACCESS_USER")).count() == 1) {
            roles.forEach(role -> {

                if (userPrinciple.getDefaultAuthorities().stream().filter(authority -> authority.appendAuthority().equals("FULL_ACCESS_USER")).count() == 1) {
                    List<AuthorityDTO> authorityDTOList = mapper.map(role.getAuthorities(), new TypeToken<List<AuthorityDTO>>() {
                    }.getType());

                    RolePermissionDTO rolePermissionDTO = RolePermissionDTO.builder()
                            .roleId(role.getId())
                            .roleName(role.getName())
                            .authorities(authorityDTOList)
                            .build();
                    responseRolePermissionList.add(rolePermissionDTO);
                }
                responseRolePermissionList.forEach(rolePermission -> {
                    List<AuthorityDTO> sortedList = rolePermission.getAuthorities().stream().sorted(Comparator.comparing(AuthorityDTO::getId)).collect(Collectors.toList());
                    rolePermission.setAuthorities(sortedList);
                });
            });
            return new ResponseEntity<>(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Get role's permissions success!", null, responseRolePermissionList), HttpStatus.ACCEPTED);
        }

        roles.forEach(role -> {
            RolePermissionDTO rolePermissionDTO = RolePermissionDTO.builder()
                    .roleId(role.getId())
                    .roleName(role.getName())
                    .authorities(new ArrayList<>())
                    .build();
            responseRolePermissionList.add(rolePermissionDTO);
        });
        return new ResponseEntity<>(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Get roles success!", null, responseRolePermissionList), HttpStatus.ACCEPTED);

//        rolePermissionDTOList.sort(Comparator.comparing(RolePermissionDTO::getId));


    }

    @Override
    public ResponseEntity<ResponseObject> deleteRole(String roleId) {
        Role role = roleRepository.findRoleById(UUID.fromString(roleId)).orElseThrow(() -> new RoleIdNotFoundException(UUID.fromString(roleId), "Not found"));
        if(!role.getName().equals("SUPER_ADMIN") && !role.getName().equals("CLASS_ADMIN") && !role.getName().equals("TRAINER") && !role.getName().equals("STUDENT") && !role.getName().equals("GUEST")){
            Role guestRole = roleRepository.findRoleByName("GUEST").orElseThrow(() -> new RoleIdNotFoundException(UUID.fromString(roleId), "Not found"));
            Set<User> users = userRepository.findAllByRole_Id(role.getId());
            if(!users.isEmpty()){
                users.forEach(user -> {
                    user.setRole(guestRole);
                    userRepository.save(user);
                });
            }
            role.setAuthorities(null);
            roleRepository.save(role);
            roleRepository.deleteById(UUID.fromString(roleId));
            return new ResponseEntity<>(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Get roles success!", null, null), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "You can not delete this role!", null, null), HttpStatus.NOT_ACCEPTABLE);
    }

}
