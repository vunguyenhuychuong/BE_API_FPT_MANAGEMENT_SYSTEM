package com.java8.tms.common.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassForm {
    @Schema(description = "class start time, hh:mm",
            example = "09:00"
            )
    private String startTime; // time frame

    @Schema(description = "class end time, hh:mm",
            example = "12:00"
            )
    private String endTime; // time frame

    @Schema(description = "class start date, dd/mm/yyyy",
            example = "02/12/2022"
            )
    private String startDate; // plan start date

    @Schema(description = "name of training class",
            example = "Fresher develop operation"
            )
    private String name;

    @Schema(description = "learning duration off class ",
            example = "4"
            )
    private int duration;

    @Schema(description = "Number of planning attendee",
            example = "10"
            )
    private int plannedAttendee;

    @Schema(description = "Number of accepted attendee",
            example = "10"
            )
    private int acceptedAttendee;

    @Schema(description = "Number of actual attendee",
            example = "09:00"
            )
    private int actualAttendee;

    @Schema(description = "Add id class admin to class",
            example = "[\n" +
                    "    \"a1901670-a354-4106-85b2-f9d3594d9933\"\n" +
                    "  ]")
    private List<UUID> accountAdminIds;

    @Schema(description = "Add id trainer to class",
            example = "[\n" +
                    "    \"f82f6c33-714e-4760-b518-872b68300888\"\n" +
                    "  ]")
    private List<UUID> accountTrainerIds;

    @Schema(description = "Add id trainee to class",
            example = "[\n" +
                    "    \"acf8ebd3-1cdc-492f-bede-4db1ead6da87\"\n" +
                    "  ]")
    private List<UUID> accountTraineeIds;

    @Schema(description = "Add id class location to class",
            example = "41c9cfa3-40c2-48a6-b2aa-a6fbcf8f2f1b"
    )
    private UUID classLocationId;

    @Schema(description = "Add id attendee level to class",
            example = "2231f77a-8b1e-43e4-b560-459ba33833d6"
    )
    private UUID attendeeLevelId;


    @Schema(description = "Add id class status to class",
            example = "1d569b2d-27fa-4457-bd26-18b668f77f49"
    )
    private UUID classStatusId;

    @Schema(description = "Add id fsu to class",
            example = "6a0f8d08-b970-4c5e-bf7f-e4f98a78e13c"
    )
    private UUID fsuId;

    @Schema(description = "Add id training program to class",
            example = "029cac58-1bdb-4ae9-a38c-99164e90930b"
    )
    private UUID trainingProgramId;
}
