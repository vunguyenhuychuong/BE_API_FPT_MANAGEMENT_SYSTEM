package com.java8.tms.common.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.FormatType;

@Repository
public interface FormatTypeRepository extends JpaRepository<FormatType, UUID> {
    @Query("SELECT f from FormatType f where f.name=?1")
    FormatType findByName(String name);

}
