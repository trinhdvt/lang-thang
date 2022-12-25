package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    private String email;

    @Column(name = "refresh_token", nullable = false, length = 100)
    private String token;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

}