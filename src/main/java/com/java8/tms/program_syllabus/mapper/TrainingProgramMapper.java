package com.java8.tms.program_syllabus.mapper;

import com.java8.tms.common.entity.TrainingProgram;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TrainingProgramMapper implements org.springframework.jdbc.core.RowMapper<TrainingProgram> {


    @Override
    public TrainingProgram mapRow(ResultSet rs, int rowNum) throws SQLException {

        return TrainingProgram.builder().id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .createdDate(rs.getDate("created_date"))
                .version(rs.getString("version")).build();

    }

}
