package com.java8.tms.common.dto;

import lombok.*;
import org.apache.commons.text.WordUtils;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Setter
public class RoleWithoutAuthorDTO {
    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return WordUtils.capitalizeFully(name.replace("_"," ").toLowerCase()) ;
    }
}
