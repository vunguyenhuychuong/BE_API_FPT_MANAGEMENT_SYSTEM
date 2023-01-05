package com.java8.tms.training_class.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TrainingClassDTO {

    @Type(type = "uuid-char")
    private UUID id;
    private String acceptedAttendee;
    private String actualAttendee;
    private Date approvedDate;
    private String courseCode;
    private Date createdDate;
    private int duration;

    private Date startTime;
    private Date endTime;

    private Date startDate;
    private Date endDate;

    private String universityCode;

    private String plannedAttendee;

    private Date reviewedDate;

    private int stt;

    private String classLocationId; // PK

    private String locationUnit;

    private int noClass;

    private Date updatedDate;

    private String updatedById;

    private String approvedById;

    private String classStatusId;

    private String createdById;

    private String attendeeLevelId;

    private String formatTypeId;

    private String fsuId;

    private String technicalGroupId;

    private String trainingProgramId;

    private String programContentId;

    private int traineeNo;

    private String trainer;

    // private String mentor;

    private String classAdmin;

    // private String formatType_Abb;

    // private int classNo_Abb;

    // private String universityCode_Abb;
    //
    // private int startYear;
}
