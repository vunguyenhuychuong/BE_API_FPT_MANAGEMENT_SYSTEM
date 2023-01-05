package com.java8.tms.common.repository;


import com.java8.tms.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findRoleByName(String name);
    @Query("select r from Role r")
    List<Role> findAll();
    Optional<Role> findRoleById(UUID id);

    List<Role> findAllByOrderById();
    @Query("select r.name from Role r where r.name not like 'Super_admin'")
    List<String> findAllRoleName();

}
