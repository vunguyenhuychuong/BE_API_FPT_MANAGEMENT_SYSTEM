package com.java8.tms.syllabus.modelMapper;

import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.meta.SyllabusStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DraftsRowMapper implements org.springframework.jdbc.core.RowMapper<Syllabus>{
    @Override
    public Syllabus mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = "";
        String code = "";
        String version = "";
        int days = 0;
        if(rs.getString("name") != null) name = rs.getString("name");
        if(rs.getString("code") != null) code = rs.getString("code");
        if(rs.getString("version") != null) version = rs.getString("version");
        if(rs.getString("days") != null) days = rs.getInt("days");
        return Syllabus.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(name)
                .code(code)
                .createdDate(rs.getDate("created_date"))
                .version(version)
                .days(days)
                .status(SyllabusStatus.values()[rs.getInt("status")])
                .createdBy(UUID.fromString(rs.getString("created_by")))
                .build();
    }
}
