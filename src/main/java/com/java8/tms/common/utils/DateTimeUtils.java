package com.java8.tms.common.utils;

import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
public class DateTimeUtils {
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    //public static final String START_DATE_FORMAT = "yyyy";

    public static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public static String now() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    public static LocalDate convertStringToLocalDate(String date) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDate.parse(date,formatter);
    }

    public static LocalTime convertStringToLocalTime(String time) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return LocalTime.parse(time,formatter);
    }
}
