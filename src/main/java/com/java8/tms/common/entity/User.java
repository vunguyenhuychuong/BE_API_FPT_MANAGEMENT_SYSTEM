package com.java8.tms.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Column(length = 50)

    @Email
    private String email;

    @Lob
    private String avatar;

    @JsonIgnore
    @Size(min = 6, max = 100)
    private String password;

    private Instant expiredDate;
    private Instant createdDate;
    private Instant updatedDate;

    //remove field userName
    //private String username;

    @Column(columnDefinition = "varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL, FULLTEXT KEY fullname(fullname)")
    private String fullname;

    private Date birthday;

    @Enumerated(EnumType.ORDINAL)
    private Gender gender;
    private String level;

    // FK
    @OneToOne
    @JoinColumn(name = "fsu_id", nullable = true, unique = true)
    private FSU fsu;

    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "createdBy")
    private List<TrainingClass> createdClasses;

    @OneToMany(mappedBy = "updatedBy")
    private List<TrainingClass> updatedClasses;

    @OneToMany(mappedBy = "reviewedBy")
    private List<TrainingClass> reviewedClasses;

    @OneToMany(mappedBy = "approvedBy")
    private List<TrainingClass> approvedClasses;

    @OneToOne(mappedBy = "user")
    private OTP otp;
}
