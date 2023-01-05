package com.java8.tms.common.repository;

import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.common.meta.TrainingProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, UUID> {
    @Query("SELECT t from TrainingProgram t where t.name=?1 and t.version=?2")
    TrainingProgram findByName(String name, String version);

    @Query("SELECT t from TrainingProgram t where t.id=?1")
    Optional<TrainingProgram> findById(UUID id);

    /**
     * <p>
     * Find All Program corresponding to the Status of a user
     * </p>
     *
     * @param userID
     * @param status
     * @return List<TrainingProgram>
     * @author Luu Thanh Huy
     */
    List<TrainingProgram> findAllByCreatedByAndStatusOrderByCreatedDateDesc(UUID userID, TrainingProgramStatus status);


    /**
     * <p>
     * Find All Program of a user by userID (createdBy)
     * </p>
     *
     * @param userID
     * @return
     * @author Luu Thanh Huy
     */
    List<TrainingProgram> findAllByCreatedBy(UUID userID);


    /**
     * <p>
     * Get Current Version of Training Program corresponding to the program name
     * </p>
     *
     * @param programName
     * @return
     * @author Luu Thanh Huy
     */
    @Query(value = "SELECT substring_index(t.version,'.',1) as current_version  "
            + "FROM training_program t "
            + "WHERE t.name = :programName "
            + "order by convert(substring_index(t.version,'.',1), UNSIGNED INTEGER) Desc "
            + "limit 1", nativeQuery = true)
    String getCurrentVersionWithProgramName(@Param("programName") String programName);


}
