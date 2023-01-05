package com.java8.tms.common.repository;

import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.meta.SyllabusStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, UUID> {
    @Query("SELECT s FROM Syllabus s WHERE NOT s.status = 2 AND NOT s.status = 3 AND NOT s.status = 4")
    Page<Syllabus> findAllSyllabuses(Pageable pageable);

    @Query("SELECT COUNT(s) FROM Syllabus s WHERE NOT s.status = 2 AND NOT s.status = 3 AND NOT s.status = 4")
    int getTotalRows();


    Syllabus findSyllabusById(UUID id);


    @Query(value = "SELECT MAX(s.version) FROM Syllabus s WHERE s.name = :name")
    String findVersionByName(@Param("name") String name);


    /**
     * <p>
     * Search Syllabus with Status and find syllabus corresponding to the keyword
     * (keyword contain syllabus name + syllabus version)
     * Returns 5 records sorted by the latest update date
     * </p>
     *
     * @param keyword
     * @param status
     * @return
     * @author Luu Thanh Huy
     */
    @Query(value = "SELECT * FROM syllabus s "
            + "Where Concat(s.name,' ',s.version) rlike :keyword "
            + "AND s.status = :status "
            + "ORDER BY s.updated_date DESC "
            + "LIMIT 5",
            nativeQuery = true)
    List<Syllabus> searchSyllabusByKeyword(@Param("keyword") String keyword, @Param("status") SyllabusStatus status);


    /**
     * <p>
     * Find all syllabus by program id
     * </p>
     *
     * @param programID
     * @return List<Syllabus>
     * @author Luu Thanh Huy
     */
    @Query(value = "SELECT s.* "
            + "FROM  syllabus s INNER JOIN program_syllabus m "
            + "ON m.syllabus_id = s.id "
            + "WHERE m.training_program_id = :programID "
            + "ORDER BY m.position ASC",
            nativeQuery = true)
    List<Syllabus> findAllSyllabusByProgramID(@Param("programID") String programID);

    @Query(
            value = "SELECT * FROM syllabus WHERE syllabus.code = code ",
            nativeQuery = true)
    List<Syllabus> findAllByCode(String code);

    @Query(
            value = "SELECT * FROM syllabus WHERE syllabus.name = name ",
            nativeQuery = true)
    List<Syllabus> findAllByName(String name);

    @Query(
            value = "SELECT * FROM syllabus WHERE syllabus.name = name ",
            nativeQuery = true)
    List<Syllabus> findAllByNameCompare(String name);

    @Modifying()
    @Query(value = "SET FOREIGN_KEY_CHECKS=0", nativeQuery = true)
    @Transactional
    void setForeignKey0();

    @Modifying()
    @Query(value = "SET FOREIGN_KEY_CHECKS=1", nativeQuery = true)
    @Transactional
    void setForeignKey1();

    @Modifying
    @Query(value = "delete material, syllabus_unit_chapter, syllabus_unit, syllabus_day, delivery_principle, assessment_scheme, syllabus\n" +
            "from material, syllabus_unit_chapter, syllabus_unit, syllabus_day, delivery_principle, assessment_scheme, syllabus\n" +
            "where material.unit_chapter_id = syllabus_unit_chapter.id\n" +
            "and syllabus_unit_chapter.unit_id = syllabus_unit.id\n" +
            "and syllabus_unit.syllabus_day_id = syllabus_day.id\n" +
            "and delivery_principle.syllabus_id = syllabus.id\n" +
            "and assessment_scheme.syllabus_id = syllabus.id\n" +
            "and syllabus_day.syllabus_id = syllabus.id\n" +
            "and syllabus.status = 3\n" +
            "and month(syllabus.created_date) < month(current_timestamp());", nativeQuery = true)
    @Transactional
    void deleteDraft();

    @Modifying
    @Query(
            value = "delete material, syllabus_unit_chapter, syllabus_unit, syllabus_day, delivery_principle, assessment_scheme, syllabus\n" +
                    "from material, syllabus_unit_chapter, syllabus_unit, syllabus_day, delivery_principle, assessment_scheme, syllabus\n" +
                    "where material.unit_chapter_id = syllabus_unit_chapter.id\n" +
                    "and syllabus_unit_chapter.unit_id = syllabus_unit.id\n" +
                    "and syllabus_unit.syllabus_day_id = syllabus_day.id\n" +
                    "and delivery_principle.syllabus_id = syllabus.id\n" +
                    "and assessment_scheme.syllabus_id = syllabus.id\n" +
                    "and syllabus_day.syllabus_id = syllabus.id\n" +
                    "and syllabus.id = :id1",
            nativeQuery = true)
    @Transactional
    void deleteById1(@Param("id1") String id);
}
