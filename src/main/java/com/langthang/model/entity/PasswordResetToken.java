package com.langthang.model.entity;

import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "password_reset_token")
@Getter
public class PasswordResetToken {
    private static final int EXPIRATION = 60 * 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token", length = 50)
    private String token;

    @OneToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    @Column(name = "expire_date")
    private Date expireDate;

    public PasswordResetToken(String token, Account account) {
        this.token = token;
        this.account = account;
        this.expireDate = DateUtils.addMinutes(new Date(), EXPIRATION);
    }

    public PasswordResetToken() {
    }

    public void refreshExpiration() {
        this.expireDate = DateUtils.addMinutes(new Date(), EXPIRATION);
    }
}