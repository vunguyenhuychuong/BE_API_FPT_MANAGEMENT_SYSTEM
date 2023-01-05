package com.java8.tms.common.repository;

import javax.transaction.Transactional;
import javax.websocket.server.PathParam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java8.tms.common.entity.ProgramSyllabus;
import com.java8.tms.common.entity.ProgramSyllabusId;

@Repository
public interface ProgramSyllabusRepository extends JpaRepository<ProgramSyllabus, ProgramSyllabusId> {
	
	/**
	 * 
	 * <p>
	 * Delete All Data In table Program_Syllabus by programID
	 * </p>
	 *
	 * @param programID
	 *
	 * @author Luu Thanh Huy
	 */
	@Modifying @Transactional
	@Query(value = "DELETE FROM program_syllabus m WHERE m.training_program_id = :programID" ,
			nativeQuery = true)
	public void deleteALLByProgramID(@PathParam("programID") String programID);
}
