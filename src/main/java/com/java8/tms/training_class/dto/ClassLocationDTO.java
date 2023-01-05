package com.java8.tms.training_class.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassLocationDTO {
    private UUID id;
    private String name;
    private List<UUID> trainingClassIds;
}
