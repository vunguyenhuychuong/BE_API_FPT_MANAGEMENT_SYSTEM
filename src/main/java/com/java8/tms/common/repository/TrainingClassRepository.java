package com.java8.tms.common.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java8.tms.common.entity.AttendeeLevel;
import com.java8.tms.common.entity.ClassLocation;
import com.java8.tms.common.entity.ClassStatus;
import com.java8.tms.common.entity.FSU;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.entity.TrainingProgram;
import org.springframework.data.jpa.repository.Query;

public interface TrainingClassRepository extends JpaRepository<TrainingClass, UUID> {

    @Query("SELECT t from TrainingClass t where t.courseCode=?1")
    TrainingClass findByCourseCode(String courseCode);

    // @Query("SELECT t from TrainingClass t where t.trainingProgram=?1")
    @Query(value = "SELECT t from TrainingClass t where t.trainingProgram=?1")
    TrainingClass findClassByTrainingProgramId(TrainingProgram trainingProgramId);

    Optional<TrainingClass> findById(UUID id);
	@Query("SELECT courseCode from TrainingClass")
	List<String> findAllClassCode();
    
    @Query("SELECT t FROM TrainingClass t WHERE t.classLocation = ?1")
	List<TrainingClass> findAllTrainingClassByLocation(ClassLocation location);

	@Query("SELECT t FROM TrainingClass t WHERE t.classStatus = ?1")
	List<TrainingClass> findAllTrainingClassByStatus(ClassStatus status);

	@Query("SELECT t FROM TrainingClass t WHERE t.attendeeLevel = ?1")
	List<TrainingClass> findAllTrainingClassByAttendee(AttendeeLevel attendee);

	@Query("SELECT t FROM TrainingClass t WHERE t.fsu = ?1")
	List<TrainingClass> findAllTrainingClassByFsu(FSU fsu);
	
	@Query("SELECT t FROM TrainingClass t WHERE t.startTime = ?1")
	List<TrainingClass> findAllTrainingClassByClassTime(TrainingClass classTime);
	
	@Query("SELECT c FROM TrainingClass c WHERE c.courseCode LIKE ?1 OR c.trainingProgram.name LIKE ?2")
	List<TrainingClass> findByCourseCodeOrTrainingProgramName(String courseCode, String programName);

	List<TrainingClass> findByCreatedDate(LocalDateTime created_Date);

	
}
