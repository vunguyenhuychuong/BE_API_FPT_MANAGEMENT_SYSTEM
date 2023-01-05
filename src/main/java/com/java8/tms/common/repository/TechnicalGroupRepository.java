package com.java8.tms.common.repository;

import com.java8.tms.common.entity.TechnicalGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TechnicalGroupRepository extends JpaRepository<TechnicalGroup, UUID> {
    @Query("SELECT t from TechnicalGroup t where t.name=?1")
    TechnicalGroup findByName(String name);

}
