package com.java8.tms.common.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.AttendeeLevel;

@Repository
public interface AttendeeLevelRepository extends JpaRepository<AttendeeLevel, UUID> {
    @Query("SELECT a from AttendeeLevel a where a.name=?1")
    AttendeeLevel findByName(String name);

}
