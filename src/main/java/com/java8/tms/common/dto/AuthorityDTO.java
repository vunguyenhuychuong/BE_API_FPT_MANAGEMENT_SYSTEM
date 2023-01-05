package com.java8.tms.common.dto;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AuthorityDTO implements Comparable<AuthorityDTO> {
    private UUID id;
    private String permission;
    private String resource;

    @Override
    public int compareTo(AuthorityDTO o) {
        if(id.compareTo(o.id) > 0) return 1;
        else if(id.compareTo(o.id) < 0) return -1;
        return 0;
    }
}
