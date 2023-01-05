package com.java8.tms.common.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.ContactPoint;

@Repository
public interface ContactPointRepository extends JpaRepository<ContactPoint, UUID>{

}
