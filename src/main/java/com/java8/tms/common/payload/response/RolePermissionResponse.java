package com.java8.tms.common.payload.response;


import com.java8.tms.common.dto.RolePermissionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {
    List<RolePermissionDTO> rolePermission;
}
