package com.java8.tms.common.repository;

import com.java8.tms.common.entity.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ClassStatusRepository extends JpaRepository<ClassStatus, UUID> {
    @Query("SELECT c from ClassStatus c where c.name=?1")
    ClassStatus findByName(String name);
}
