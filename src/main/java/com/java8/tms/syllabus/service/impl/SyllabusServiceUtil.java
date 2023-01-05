package com.java8.tms.syllabus.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SyllabusServiceUtil {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getSQLForSortingAllSyllabuses(int page, int size, String sortBy, String sortType) {
        return "SELECT * FROM syllabus s WHERE s.status != 2 AND s.status != 3 AND s.status != 4"
                + " ORDER BY s." + sortBy.toLowerCase() + " " + sortType
                + getLimitAndOffsetValues(page, size);
    }

    public String getSQLForSearchingByAllCreatedDate(String startDate, String endDate) {
        String[] startDateArr = startDate.split("/");
        String[] endDateArr = endDate.split("/");
        String strStartDate = startDateArr[2] + "-" + startDateArr[1] + "-" + startDateArr[0] + " 00:00:00";
        String strEndDate = endDateArr[2] + "-" + endDateArr[1] + "-" + endDateArr[0] + " 00:00:00";
        return "SELECT * FROM syllabus s WHERE NOT (s.status = 2 OR s.status = 3 OR s.status = 4) "
                + "AND (s.created_date BETWEEN '" + strStartDate + "' AND '" + strEndDate + "')";
    }

    public String getSQLForSearchingByCreatedDate(String startDate, String endDate, int page, int size) {
        String[] startDateArr = startDate.split("/");
        String[] endDateArr = endDate.split("/");
        String strStartDate = startDateArr[2] + "-" + startDateArr[1] + "-" + startDateArr[0] + " 00:00:00";
        String strEndDate = endDateArr[2] + "-" + endDateArr[1] + "-" + endDateArr[0] + " 00:00:00";
        return "SELECT * FROM syllabus s WHERE NOT (s.status = 2 OR s.status = 3 OR s.status = 4) "
                + "AND (s.created_date BETWEEN '" + strStartDate + "' AND '" + strEndDate + "')"
                + getLimitAndOffsetValues(page, size);
    }

    public String getTotalRowsForSearchingByCreatedDate(String startDate, String endDate) {
        String[] startDateArr = startDate.split("/");
        String[] endDateArr = endDate.split("/");
        String strStartDate = startDateArr[2] + "-" + startDateArr[1] + "-" + startDateArr[0] + " 00:00:00";
        String strEndDate = endDateArr[2] + "-" + endDateArr[1] + "-" + endDateArr[0] + " 00:00:00";
        return "SELECT COUNT(*) FROM syllabus s WHERE NOT (s.status = 2 OR s.status = 3 OR s.status = 4) "
                + "AND (s.created_date BETWEEN '" + strStartDate + "' AND '" + strEndDate + "')";
    }

    public String getSQLForSearchingByCreatedDateAndSort(String startDate, String endDate, int page, int size,
                                                         String sortBy, String sortType) {
        String[] startDateArr = startDate.split("/");
        String[] endDateArr = endDate.split("/");
        String strStartDate = startDateArr[2] + "-" + startDateArr[1] + "-" + startDateArr[0] + " 00:00:00";
        String strEndDate = endDateArr[2] + "-" + endDateArr[1] + "-" + endDateArr[0] + " 00:00:00";
        return "SELECT * FROM syllabus s WHERE NOT (s.status = 2 OR s.status = 3 OR s.status = 4) "
                + "AND (s.created_date BETWEEN '" + strStartDate + "' AND '" + strEndDate + "')"
                + " ORDER BY s." + sortBy.toLowerCase() + " " + sortType
                + getLimitAndOffsetValues(page, size);
    }

    public String getSQLForSearchingByCreatedDateAndSortByCreatedBy(String startDate, String endDate, int page, int size,
                                                                    String sortType){
        String[] startDateArr = startDate.split("/");
        String[] endDateArr = endDate.split("/");
        String strStartDate = startDateArr[2] + "-" + startDateArr[1] + "-" + startDateArr[0] + " 00:00:00";
        String strEndDate = endDateArr[2] + "-" + endDateArr[1] + "-" + endDateArr[0] + " 00:00:00";
        return "SELECT * FROM syllabus s JOIN user u ON s.created_by = u.id WHERE NOT (s.status = 2 OR s.status = 3 OR s.status = 4) "
                + "AND (s.created_date BETWEEN '" + strStartDate +"' AND '" + strEndDate + "')"
                + " ORDER BY u.fullname " + sortType + getLimitAndOffsetValues(page, size);
    }

    public String getSQLForSortingAllSyllabusesByCreatedBy(int page, int size, String sortType) {
        return "SELECT * FROM syllabus s JOIN user u ON s.created_by = u.id WHERE NOT "
                + "(s.status = 2 OR s.status = 3 OR s.status = 4)"
                + " ORDER BY u.fullname " + sortType + getLimitAndOffsetValues(page, size);
    }

    public String getSQLForSearchingByKeywords(int page, int size, String keyword) {
        return "SELECT * FROM syllabus s JOIN user u ON s.created_by = u.id WHERE NOT "
                + "(s.status = 2 OR s.status = 3 OR s.status = 4) AND "
                + "(s.name = '" + keyword + "' OR s.code = '" + keyword + "' OR u.fullname = '"
                + keyword + "')" + getLimitAndOffsetValues(page, size);
    }
    public String getSQLForSearchingByKeywordsForSuggestions(int page, int size, String keyword) {
        return "SELECT * FROM syllabus s JOIN user u ON s.created_by = u.id WHERE NOT "
                + "(s.status = 2 OR s.status = 3 OR s.status = 4) AND "
                + "(s.name LIKE '%" + keyword + "%' OR s.code LIKE '%" + keyword + "%' OR u.fullname LIKE '%"
                + keyword + "%')" + getLimitAndOffsetValues(page, size);
    }

    public String getTotalRowsForSearchingByKeywords(String keyword) {
        return "SELECT * FROM syllabus s JOIN user u ON s.created_by = u.id WHERE NOT "
                + "(s.status = 2 OR s.status = 3 OR s.status = 4) AND "
                + "(s.name = '" + keyword + "' OR s.code = '" + keyword + "' OR u.fullname = '"
                + keyword + "')";
    }

    public String getLimitAndOffsetValues(int page, int size) {
        return " LIMIT " + size + " OFFSET " + (page * size);
    }

    public String getSQLForSortingAllDraft(UUID userid, int page, int size, String sortType) {
        return "SELECT * FROM syllabus s WHERE s.created_by = '" + userid +
                "' AND s.status = 3 ORDER BY s.created_date " + sortType
                + getLimitAndOffsetValues(page, size);
    }
}
