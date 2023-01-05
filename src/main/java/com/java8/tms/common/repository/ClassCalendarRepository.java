package com.java8.tms.common.repository;

import com.java8.tms.common.entity.ClassCalendar;
import com.java8.tms.common.entity.TrainingClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClassCalendarRepository extends JpaRepository<ClassCalendar, UUID> {

    @Query("SELECT c FROM ClassCalendar c WHERE CAST(c.dateTime as date) = CAST(?1 as date) AND (c.trainingClass.courseCode LIKE ?2 OR c.trainingClass.trainingProgram.name LIKE ?3)")
    List<ClassCalendar> findByDateTimeAndTrainingClass_CourseCodeOrTrainingClass_TrainingProgram_Name(LocalDateTime datetime, String courseCode, String programName);

    @Query("SELECT c FROM ClassCalendar c WHERE c.trainingClass.courseCode LIKE ?1 OR c.trainingClass.trainingProgram.name LIKE ?2")
    List<ClassCalendar> findByTrainingClass_CourseCodeOrTrainingClass_TrainingProgram_Name(String courseCode, String programName);

	List<ClassCalendar> findByDateTime(LocalDateTime created_Date);
}
