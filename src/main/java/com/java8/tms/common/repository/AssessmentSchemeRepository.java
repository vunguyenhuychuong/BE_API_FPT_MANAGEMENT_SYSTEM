package com.java8.tms.common.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.AssessmentScheme;

@Repository
public interface AssessmentSchemeRepository extends JpaRepository<AssessmentScheme, UUID>{

}