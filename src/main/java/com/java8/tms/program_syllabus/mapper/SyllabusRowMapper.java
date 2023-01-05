package com.java8.tms.program_syllabus.mapper;

import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.meta.SyllabusStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SyllabusRowMapper implements org.springframework.jdbc.core.RowMapper<Syllabus> {

    @Override
    public Syllabus mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Syllabus.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .code(rs.getString("code"))
                .createdDate(rs.getDate("created_date"))
                .version(rs.getString("version"))
                .attendeeNumber(rs.getInt("attendee_number"))
                .days(rs.getInt("days"))
                .status(SyllabusStatus.values()[rs.getInt("status")])
                .hours(rs.getInt("hours"))
                .updatedDate(rs.getDate("updated_date"))
                .updatedBy(UUID.fromString(rs.getString("updated_by")))
                .createdBy(UUID.fromString(rs.getString("created_by")))
                .build();


    }

}
