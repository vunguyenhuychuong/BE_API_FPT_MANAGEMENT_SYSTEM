package com.java8.tms.common.repository;

import com.java8.tms.common.entity.ClassLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ClassLocationRepository extends JpaRepository<ClassLocation, UUID> {
    @Query("SELECT c from ClassLocation c where c.name=?1")
    ClassLocation findByName(String name);

}
