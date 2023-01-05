package com.java8.tms.common.repository;

import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u from User u where u.email=?1")
    User findUserByEmail(String email);

    //     @Modifying
//     @Transactional
//     @Query(value = "insert into class_trainers (class_id, trainer_id) VALUES (?1, ?2)", nativeQuery = true)
//     void InsertIntoClassTrainer(UUID classId,UUID trainerId);
    @Modifying
    @Query(value = "insert into class_trainers(class_id,trainer_id) VALUES (:classId,:trainerId)", nativeQuery = true)
    @Transactional
    void InsertIntoClassTrainer(@Param("classId") UUID classId, @Param("trainerId") UUID id);

    @Modifying
    @Query(value = "insert into class_admins(class_id,admin_id) VALUES (:classId,:adminId)", nativeQuery = true)
    @Transactional
    void InsertIntoClassAdmin(@Param("classId") UUID classId, @Param("adminId") UUID id);


    Optional<User> findByEmail(String email);


    User getUserByEmail(String email);

    //new code from user
    List<User> findByFullnameLike(String fullname);

    Boolean deleteByEmail(String email);

    Optional<User> findByEmailEquals(String email);

    Page<User> findAllByStatusIsNot(Pageable pageable, UserStatus status);

    @Modifying
    @Transactional
    @Query(value = "delete from User u where u.email=:email")
    void deleteUserByEmail(String email);


    Boolean existsByEmail(String email);

    Set<User> findAllByRole_Id(UUID roleId);

    @Query(value = "select u.fullname from User u where u.role.name=:role")
    List<String> findAllByRole_Name(String role);
}
