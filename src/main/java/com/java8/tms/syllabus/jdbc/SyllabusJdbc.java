package com.java8.tms.syllabus.jdbc;

import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.program_syllabus.mapper.SyllabusRowMapper;
import com.java8.tms.syllabus.modelMapper.DraftsRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyllabusJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Syllabus> getSyllabuses(String query) {
        return jdbcTemplate.query(query, new SyllabusRowMapper());
    }

    public List<Syllabus> getDrafts(String query) {
        return jdbcTemplate.query(query, new DraftsRowMapper());
    }
    public int getTotalRows(String query){return jdbcTemplate.queryForObject(query, Integer.class);}
}
