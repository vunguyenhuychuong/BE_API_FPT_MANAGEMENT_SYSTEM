package com.java8.tms.program_syllabus.jdbc;

import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.program_syllabus.mapper.SyllabusRowMapper;
import com.java8.tms.program_syllabus.mapper.TrainingProgramMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProgramSyllabusJDBC {

    public static final String SEARCH_SYLLABUS = "SELECT "
            + " id, name, code, version, attendee_number, days, hours, status, created_date, created_by, updated_date, updated_by "
            + " FROM syllabus WHERE name =TRIM(?) AND code =TRIM(?) AND version like ?";
    public static final String SEARCH_NEWEST_VERSION_OF_TRAININGPROGRAM = "select * " + " From training_program m "
            + " WHERE m.version like concat( ( " + " SELECT substring_index(t.version,'.',1) "
            + " FROM training_program t " + " WHERE t.name = ? "
            + " order by convert(substring_index(t.version,'.',1), UNSIGNED INTEGER) Desc " + " limit 1),'%') "
            + " AND  m.name = ?" + " Order by m.updated_date Desc limit 1";
    private final JdbcTemplate jdbcTemplate;

    public ProgramSyllabusJDBC(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * <p>
     * Get Newest Version of Training Program
     * </p>
     *
     * @param name
     * @return
     * @author Nguyen Quoc Bao
     */
    public Optional<TrainingProgram> findNewestVersionOfTrainingProgram(String name) {
        String sql = SEARCH_NEWEST_VERSION_OF_TRAININGPROGRAM;
        return jdbcTemplate.query(sql, new TrainingProgramMapper(), name, name).stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */

    public Optional<Syllabus> findAvailableSyllabus(Syllabus syllabus) {
        String sql = SEARCH_SYLLABUS;

        if (syllabus.getName() == null || syllabus.getCode() == null) {

            syllabus.setName("");
            syllabus.setCode("");
        }
        if (syllabus.getVersion() == null || syllabus.getVersion().isEmpty() || syllabus.getVersion().isBlank()) {
            syllabus.setVersion("%");
            sql += " ORDER BY updated_date";
        }
        sql += " limit 1";

        return jdbcTemplate.query(sql, new SyllabusRowMapper(), syllabus.getName(), syllabus.getCode(),
                syllabus.getVersion()).stream().findFirst();

    }


}
