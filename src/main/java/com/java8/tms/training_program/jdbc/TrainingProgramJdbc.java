package com.java8.tms.training_program.jdbc;

import com.java8.tms.common.meta.TrainingProgramStatus;
import com.java8.tms.training_program.dto.TrainingProgramForFilter;
import com.java8.tms.training_program.meta.ColumnTrainingProgramTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TrainingProgramJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * <p>
     * Get result list and mapping its column with table of training program
     * </p>
     *
     * @param query {@code String}
     * @return result list from query table training program
     * @author Pham Xuan Kien
     */
    public List<TrainingProgramForFilter> getTrainingProgramForFiltersByQuery(String query) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            TrainingProgramForFilter trainingProgram = new TrainingProgramForFilter();
            trainingProgram.setId(UUID.fromString(rs.getString(ColumnTrainingProgramTable.ID.toString().toLowerCase())));
            trainingProgram.setName(rs.getString(ColumnTrainingProgramTable.NAME.toString().toLowerCase()));
            trainingProgram.setCreatedDate(sdf.format(rs.getDate(ColumnTrainingProgramTable.CREATED_DATE.toString().toLowerCase())));
            trainingProgram.setCreatedBy(rs.getString(ColumnTrainingProgramTable.CREATED_BY.toString().toLowerCase()));
            trainingProgram.setDuration(rs.getInt(ColumnTrainingProgramTable.DURATION.toString().toLowerCase()));
            trainingProgram.setStatus(StringUtils
                    .capitalize(TrainingProgramStatus.values()[rs.getInt(ColumnTrainingProgramTable.STATUS.toString().toLowerCase())].toString().toLowerCase()));
            return trainingProgram;
        });
    }

    /**
     * <p>
     * Return total found result of the input query
     * </p>
     *
     * @param query {@code String}
     * @return total found result
     * @author Pham Xuan Kien
     */
    public Long getTotalFoundResult(String query) {
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    /**
     * <p>
     * Return a matching keyword list for choosing when user enter a keyword
     * </p>
     *
     * @param keyword {@code String}
     * @return a response entity with matching keyword list
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    public List<String> getKeywordList(String query, String keyword) {
        List<String> keywordList = new ArrayList<>();
        List<String> queryResult = jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("keyword"));

        if (!queryResult.isEmpty()) {
            //if there are no found result, return empty list to show user that there are no such result match that keyword
            //else, add the inputed keyword to result list as first keyword for user if they want to choose what they did input
            keywordList.add(keyword);
            //add the rest to result list as
            keywordList.addAll(1, queryResult);
        }

        return keywordList;
    }
}
