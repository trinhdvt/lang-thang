package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String refreshToken;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

}