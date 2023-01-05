package com.java8.tms;

import com.java8.tms.common.repository.TrainingClassRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TrainingClassTest {

    final private static String NAME_STATUS_DELETE_IN_DATABASE = "Delete";
    final private static String NAME_STATUS_DEACTIVE_IN_DATABASE = "Closed";
    @Autowired
    private TrainingClassRepository trainingClassRepository;

    @Test
    void checkDeleteClass() {
        //input id of class have been change status to DELETE to check result
        String class_id = "040d27a1-e7f7-4729-8f8a-f0b3146f5cd6";

        String actual = trainingClassRepository.findById(UUID.fromString(class_id)).get().getClassStatus().getName();
        System.out.println("==> actual status: " + actual);

        assertEquals(NAME_STATUS_DELETE_IN_DATABASE, actual);
    }

    @Test
    void checkDeActiveClass() {
        //input id of class have been change status to DEACTIVE to check result
        String class_id = "040d27a1-e7f7-4729-8f8a-f0b3146f5cd6";

        String actual = trainingClassRepository.findById(UUID.fromString(class_id)).get().getClassStatus().getName();
        System.out.println("==> actual status: " + actual);

        assertEquals(NAME_STATUS_DEACTIVE_IN_DATABASE, actual);
    }

}
