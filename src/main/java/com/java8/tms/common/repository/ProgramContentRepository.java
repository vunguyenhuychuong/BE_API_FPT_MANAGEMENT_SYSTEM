package com.java8.tms.common.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.ProgramContent;

@Repository
public interface ProgramContentRepository extends JpaRepository<ProgramContent, UUID> {
    @Query("SELECT p from ProgramContent p where p.name=?1")
    ProgramContent findByName(String name);

}
