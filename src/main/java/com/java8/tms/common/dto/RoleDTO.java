package com.java8.tms.common.dto;

import lombok.*;
import org.apache.commons.text.WordUtils;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Setter
public class RoleDTO implements Serializable, Comparable<RoleDTO> {
    private UUID id;
    private String name;
    private Set<AuthorityDTO> authorities;

    public UUID getId() {
        return id;
    }

    public String getName() {
       return WordUtils.capitalizeFully(name.replace("_"," ").toLowerCase()) ;
    }

    public Set<AuthorityDTO> getAuthorities() {
        return authorities;
    }

    @Override
    public int compareTo(RoleDTO o) {
        if(id.compareTo(o.id) > 0) return 1;
        else if(id.compareTo(o.id) < 0) return -1;
        return 0;
    }
}
