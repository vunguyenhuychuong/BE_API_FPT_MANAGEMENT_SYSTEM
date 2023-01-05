package com.java8.tms.common.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.DeliveryType;

@Repository
public interface DeliveryTypeRepository extends JpaRepository<DeliveryType, UUID> {
    @Query(value = "SELECT * FROM delivery_type WHERE delivery_type.name LIKE ?1", nativeQuery = true)
    List<DeliveryType> findByName(String name);
}
