package com.java8.tms.common.meta;

import java.util.HashSet;
import java.util.Set;

public enum UserStatus {
    DEACTIVE, ACTIVE, OFF_CLASS, IN_CLASS, ON_BOARDING, DELETE;

    public static Set<String> getAllValueUserStatus() {
        UserStatus[] statuses = UserStatus.values();
        Set<String> listStatus = new HashSet<>();
        for (UserStatus status : statuses) {
            listStatus.add(status.toString());
        }
        return listStatus;
    }
}
