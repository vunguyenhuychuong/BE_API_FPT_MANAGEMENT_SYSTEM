package com.java8.tms.common.repository;

import com.java8.tms.common.entity.FSU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FSURepository extends JpaRepository<FSU, UUID> {
    @Query("SELECT f from FSU f where f.name=?1")
    FSU findByName(String name);
}
