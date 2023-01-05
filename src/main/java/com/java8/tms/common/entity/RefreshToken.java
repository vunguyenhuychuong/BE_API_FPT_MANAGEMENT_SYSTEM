package com.java8.tms.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    @NotNull
    private Instant expiryDate;


    public RefreshToken(RefreshToken refreshToken) {
        this.id = refreshToken.getId();
        this.user = refreshToken.user;
        this.token = refreshToken.getToken();
        this.expiryDate = refreshToken.getExpiryDate();
    }
}
