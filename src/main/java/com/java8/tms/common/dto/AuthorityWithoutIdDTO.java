package com.java8.tms.common.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AuthorityWithoutIdDTO  {
    private String permission;
    private String resource;
}
