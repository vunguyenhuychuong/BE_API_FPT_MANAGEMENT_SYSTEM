package com.java8.tms.training_class.utils;

import java.util.*;

public class DataUtil
{
    public static Date endDate(Date date, int months)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DATE, -1);//minus number would decrement the date
        return cal.getTime();
    }
    public static Set<String> splitString(String s) {
        String[] result = s.split(",");
        Set<String> list=new HashSet<String>();

        for (String value:result
        ) {
            list.add(value.trim());
        }

        return list;
    }
}
