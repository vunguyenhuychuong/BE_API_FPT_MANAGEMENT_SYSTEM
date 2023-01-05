package com.java8.tms.training_program.service.impl;

import com.java8.tms.common.dto.TrainingProgramDTO;
import com.java8.tms.common.meta.TrainingProgramStatus;
import com.java8.tms.training_program.dto.RequestForFilterTrainingProgram;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.training_program.meta.ColumnTrainingProgramTable;
import com.java8.tms.training_program.validation.ValidationOfRequestForFilterTrainingProgram;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TrainingProgramServiceUtil {
    private final JdbcTemplate jdbcTemplate;
    private final ValidationOfRequestForFilterTrainingProgram validationOfRequestForFilterTrainingProgram;

    public TrainingProgramServiceUtil(JdbcTemplate jdbcTemplate, ValidationOfRequestForFilterTrainingProgram validationOfRequestForFilterTrainingProgram) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationOfRequestForFilterTrainingProgram = validationOfRequestForFilterTrainingProgram;
    }

    /**
     * <p>
     * Get result list and apping its column with table of training program
     * </p>
     *
     * @param query
     * @return
     * @author Tung Nguyen
     */

    public List<TrainingProgramDTO> getTrainingProgramByQuery(String query) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            TrainingProgramDTO trainingProgram = new TrainingProgramDTO();
            trainingProgram.setId(UUID.fromString(rs.getString(ColumnTrainingProgramTable.ID.toString().toLowerCase())));
            trainingProgram.setName(rs.getString(ColumnTrainingProgramTable.NAME.toString().toLowerCase()));
            if (trainingProgram.getName() == null) trainingProgram.setName("");
            trainingProgram.setCreatedDate(sdf.format(rs.getDate(ColumnTrainingProgramTable.CREATED_DATE.toString())));
            trainingProgram.setCreatedBy(UUID.fromString(rs.getString(ColumnTrainingProgramTable.CREATED_BY.toString())));
            trainingProgram.setVersion(rs.getString(ColumnTrainingProgramTable.VERSION.name()));
            if (trainingProgram.getCreatedBy() == null) trainingProgram.setCreatedBy(UUID.fromString(""));
            trainingProgram.setStatus(TrainingProgramStatus.valueOf(StringUtils
                    .capitalize(TrainingProgramStatus.values()[rs.getInt(ColumnTrainingProgramTable.STATUS.toString())].toString())));
            return trainingProgram;
        });
    }

    /**
     * <p>
     * Get result in a page with size is chosen
     * </p>
     *
     * @param page
     * @param size
     * @return
     * @author Tung Nguyen
     */
    public String constructSQLForResultInAPage(int page, int size) {

        String sqlForResultInAPage =
                "SELECT DISTINCT tp.id, CONCAT(tp.name, \"_\",tp.version) AS name, tp.created_date, u.id AS created_by, SUM(s.days) AS duration, tp.status, tp.version "
                        + "FROM "
                        + "user u "
                        + "RIGHT JOIN training_program tp ON u.id = tp.created_by "
                        + "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id "
                        + "LEFT JOIN syllabus s ON s.id = ps.syllabus_id "
                        + "WHERE ((tp.status <> 2 AND tp.status <> 3)\r\n"
                        + "    OR (tp.status = NULL)) "
                        + "GROUP BY tp.id, tp.name, tp.created_date, created_by, tp.status";
        sqlForResultInAPage += " LIMIT " + size + " OFFSET " + (page * size);
        return sqlForResultInAPage;
    }

    /**
     * <p>
     * Get the number of total records of training program
     * </p>
     *
     * @return
     * @author Tung Nguyen
     */
    public String constructSQLForTotalRows() {
        return "SELECT COUNT(DISTINCT tp.id) \n" +
                "FROM \n" +
                "user u \n" +
                "RIGHT JOIN training_program tp ON u.id = tp.created_by \n" +
                "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id \n" +
                "LEFT JOIN syllabus s ON s.id = ps.syllabus_id"
                + " WHERE ((tp.status <> 2 AND tp.status <> 3)\r\n"
                + "    OR (tp.status = NULL)) ";
    }

    /**
     * <p>
     * Return a constructed keyword string array as query parts to filter training program
     * </p>
     *
     * @param keywordArray {@code String[]) to construct
     * @return a constructed keyword string array for filter
     * first element is like case when part of training program's name
     * second element is like case when part of user's fullname
     * third element is like case when part of suggested keyword
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    public String[] constructKeywordForFilterTrainingProgram(String[] keywordArray) {

        if (keywordArray == null) keywordArray = new String[]{};

        String keywordsAsString = String.join(" ", keywordArray).replaceAll("\\s+", " ").trim();
        String[] keywordsAsArray = keywordsAsString.toLowerCase().split("[\\s\\_]");
        Set<String> keywordsSet = new HashSet<>();
        for (String keyword : keywordsAsArray) {
            keywordsSet.add(keyword);
        }
        List<String> caseWhenPartsForTrainingProgramName = new ArrayList<>();
        List<String> caseWhenPartsForUserFullname = new ArrayList<>();
        List<String> caseWhenPartsForKeyword = new ArrayList<>();

        String likeCorePartForTrainingProgramName;
        String likeCorePartForUserFullname;
        String likeCorePartForKeyword;

        if (keywordsAsString.isBlank()) {
            likeCorePartForTrainingProgramName = "";
            likeCorePartForUserFullname = "";
            likeCorePartForKeyword = "";
        } else {

            for (String keyword : keywordsSet) {
                caseWhenPartsForTrainingProgramName.add(
                        "(CASE WHEN CONCAT(tp.name, \"_\", tp.version) Like '% " + keyword + " %' OR CONCAT(tp.name, \"_\", tp.version) Like '% " + keyword + "' OR \r\n"
                                + "CONCAT(tp.name, \"_\", tp.version) Like '" + keyword + " %' OR CONCAT(tp.name, \"_\", tp.version) = '" + keyword + "' THEN 1 ELSE 0 END)\r\n"
                                + "+\r\n"
                                + "(CASE WHEN CONCAT(tp.name, \"_\", tp.version) Like '%" + keyword + "%' THEN 0.5 ELSE 0 END)\r\n");

                caseWhenPartsForUserFullname.add(
                        "(CASE WHEN u.fullname Like '% " + keyword + " %' OR u.fullname Like '% " + keyword + "' OR \r\n"
                                + "u.fullname Like '" + keyword + " %' OR u.fullname = '" + keyword + "' THEN 1 ELSE 0 END)\r\n"
                                + "+\r\n"
                                + "(CASE WHEN u.fullname Like '%" + keyword + "%' THEN 0.5 ELSE 0 END)\r\n");

                caseWhenPartsForKeyword.add(
                        "(CASE WHEN keyword Like '% " + keyword + " %' OR keyword Like '% " + keyword + "' OR \r\n"
                                + "keyword Like '" + keyword + " %' OR keyword = '" + keyword + "' THEN 1 ELSE 0 END)\r\n"
                                + "+\r\n"
                                + "(CASE WHEN keyword Like '%" + keyword + "%' THEN 0.5 ELSE 0 END)\r\n");
            }

            likeCorePartForTrainingProgramName = String.join("+\r\n", caseWhenPartsForTrainingProgramName);
            likeCorePartForUserFullname = String.join("+\r\n", caseWhenPartsForUserFullname);
            likeCorePartForKeyword = String.join("+\r\n", caseWhenPartsForKeyword);
        }

        return new String[]{likeCorePartForTrainingProgramName, likeCorePartForUserFullname, likeCorePartForKeyword};
    }

    /**
     * <p>
     * Return a constructed string as a query for getting total rows of filter
     * result
     * </p>
     *
     * @param requestData   {@code String[])
     * @param ordinalStatus {@code Integer)
     * @return a constructed string
     * @throws InvalidRequestForFilterTrainingProgramException
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    public String constructSQLForTotalRows(RequestForFilterTrainingProgram requestData) throws InvalidRequestForFilterTrainingProgramException {

        validationOfRequestForFilterTrainingProgram.validateRequestForFilterTrainingProgram(requestData);

        String[] caseWhenParts = constructKeywordForFilterTrainingProgram(requestData.getSearchValue());
        String caseWhenPart = !caseWhenParts[0].equals("") ? caseWhenParts[0] + "+\r\n" + caseWhenParts[1] : "";

        //parse status from enum to its ordinal
        TrainingProgramStatus status;
        Integer ordinalStatus = null;
        if (requestData.getStatus() != null) {
            status = TrainingProgramStatus.valueOf(requestData.getStatus().toUpperCase());
            ordinalStatus = status.ordinal();
        }

        return "SELECT COUNT(r.id) FROM ("
                + "SELECT DISTINCT tp.id " + (!caseWhenParts[0].equals("") ? ", " + caseWhenPart + (" as score ") : (""))
                + "FROM user u "
                + "RIGHT JOIN training_program tp ON u.id = tp.created_by "
                + "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id "
                + "LEFT JOIN syllabus s ON s.id = ps.syllabus_id "
                + "WHERE "
                + " tp.created_date IS NOT NULL AND ((" + ordinalStatus + " is null AND tp.status <> 2 AND tp.status <> 3)\r\n"
                + "    OR (tp.status = " + ordinalStatus + ")) "
                + "GROUP BY tp.id, tp.name, tp.version, u.fullname "
                + (!caseWhenParts[0].equals("") ? ("HAVING score > 0 ") : ("")) + ") r";

//        return
//                "SELECT COUNT(DISTINCT tp.id) "
//                        + "FROM user u "
//                        + "RIGHT JOIN training_program tp ON u.id = tp.created_by "
//                        + "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id "
//                        + "LEFT JOIN syllabus s ON s.id = ps.syllabus_id "
//                        + "WHERE " + caseWhenPart + (!caseWhenParts[0].equals("") ? (" > 0 AND ") : (""))
//                        + " tp.created_date IS NOT NULL AND ((" + ordinalStatus + " is null AND tp.status <> 2 AND tp.status <> 3)\r\n"
//                        + "    OR (tp.status = " + ordinalStatus + ")) ";
    }

    /**
     * <p>
     * Return a query to get suggested keyword list
     * </p>
     *
     * @param requestData {@code String}
     * @return a query to get suggested keyword list
     * @author Pham Xuan Kien
     */
    public String constructSQLForSuggestedKeywords(String requestData) {

        String[] caseWhenParts = constructKeywordForFilterTrainingProgram(new String[]{requestData});

        return
                "(SELECT CONCAT(tp.name, \"_\",tp.version) AS keyword, " + caseWhenParts[0] + " AS score "
                        + "FROM "
                        + "user u \r\n"
                        + "RIGHT JOIN training_program tp ON u.id = tp.created_by \r\n"
                        + "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id \r\n"
                        + "LEFT JOIN syllabus s ON s.id = ps.syllabus_id  "
                        + "WHERE "
                        + " tp.created_date IS NOT NULL AND (tp.status <> 2 AND tp.status <> 3)\r\n"
                        + "GROUP BY tp.name, tp.version, keyword "
                        + "HAVING score > 0)"
                        + " UNION " +
                        "(SELECT u.fullname AS keyword, " + caseWhenParts[1] + " AS score "
                        + "FROM "
                        + "user u \r\n"
                        + "RIGHT JOIN training_program tp ON u.id = tp.created_by \r\n"
                        + "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id \r\n"
                        + "LEFT JOIN syllabus s ON s.id = ps.syllabus_id  "
                        + "WHERE "
                        + " tp.created_date IS NOT NULL AND (tp.status <> 2 AND tp.status <> 3)\r\n"
                        + "GROUP BY u.fullname, keyword "
                        + "HAVING score > 0)"
                        + " ORDER BY " + caseWhenParts[2] + " DESC limit 7";
    }

    /**
     * <p>
     * Return a constructed string as a sql for filter result in a page
     * </p>
     *
     * @param requestData {@code RequestForFilterTrainingProgram}
     * @return a constructed string
     * @throws InvalidRequestForFilterTrainingProgramException
     * @author Pham Xuan Kien, Le Tri Quyen, Le Vu Lam Duy
     */
    public String constructSQLForResultInAPage(RequestForFilterTrainingProgram requestData) throws InvalidRequestForFilterTrainingProgramException {

        validationOfRequestForFilterTrainingProgram.validateRequestForFilterTrainingProgram(requestData);

        String[] caseWhenParts = constructKeywordForFilterTrainingProgram(requestData.getSearchValue());
        String caseWhenPart = !caseWhenParts[0].equals("") ? caseWhenParts[0] + "+\r\n" + caseWhenParts[1] : "";
        //parse status from enum to its ordinal
        TrainingProgramStatus status;
        Integer ordinalStatus = null;
        if (requestData.getStatus() != null) {
            status = TrainingProgramStatus.valueOf(requestData.getStatus().toUpperCase());
            ordinalStatus = status.ordinal();
        }

        String sortBy = requestData.getSortBy();
        String sortIn = requestData.getSortType();

        int page = requestData.getPage() - 1;
        int size = requestData.getSize();

        String sqlForResultInAPage =
                "SELECT DISTINCT tp.id, CONCAT(tp.name, \"_\",tp.version) AS name, tp.created_date, u.fullname AS created_by, SUM(CASE WHEN s.status = 0 THEN s.days ELSE 0 END) AS duration, tp.status " + (!caseWhenParts[0].equals("") ? ", " + caseWhenPart + (" as score ") : (""))
                        + "FROM user u "
                        + "RIGHT JOIN training_program tp ON u.id = tp.created_by "
                        + "LEFT JOIN program_syllabus ps ON tp.id = ps.training_program_id "
                        + "LEFT JOIN syllabus s ON s.id = ps.syllabus_id "
                        + "WHERE "
                        + " tp.created_date IS NOT NULL AND ((" + ordinalStatus + " is null AND tp.status <> 2 AND tp.status <> 3)\r\n"
                        + "    OR (tp.status = " + ordinalStatus + ")) "
                        + "GROUP BY tp.id, tp.name, tp.version, tp.created_date, u.fullname, tp.status "
                        + (!caseWhenParts[0].equals("") ? ("HAVING score > 0 ") : (""));

        //add sort part to the query
        if (sortBy != null && sortIn != null) {
            sqlForResultInAPage += " ORDER BY " + sortBy.toLowerCase() + " " + sortIn;
        } else {

            sqlForResultInAPage += !caseWhenParts[0].equals("") ? " ORDER BY score DESC" : "";
        }

        sqlForResultInAPage += " LIMIT " + size + " OFFSET " + (page * size);

        return sqlForResultInAPage;
    }
}
