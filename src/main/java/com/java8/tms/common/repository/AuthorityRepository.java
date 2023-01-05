package com.java8.tms.common.repository;


import com.java8.tms.common.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Optional<Authority> findOneByPermissionAndResource(String permission, String resource);
    Optional<Authority> findOneById(UUID id);
    List<Authority> findAllByOrderByResource();
//select a.permission from role_authority r join authority a on r.authority_id = a.id where r.role_id=:role_id and a.resource=:resource
    //select role_authority.role_id from role_authority where role_authority.role_id = ?1
    @Modifying
    @Query(value = "select a.permission from role_authority r join authority a on r.authority_id = a.id where r.role_id=?1 and a.resource=?2", nativeQuery = true)
    List<String> finAllByRoleIdAndResource(String role_id, String resource);

    @Query(value = "select a.permission from role_authority r join authority a on r.authority_id = a.id where r.role_id=?1 and a.resource=?2", nativeQuery = true)
    String finAllByRoleIdAndResource2(String role_id, String resource);
}
