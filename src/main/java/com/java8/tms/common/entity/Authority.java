package com.java8.tms.common.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;

    @NotBlank
    private String permission;

    @NotBlank
    private String resource;

    public String appendAuthority(){
        return this.permission.toUpperCase().trim() + "_" +this.resource.toUpperCase().trim();
    }

}
