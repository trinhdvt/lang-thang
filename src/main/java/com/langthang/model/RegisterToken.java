package com.langthang.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "register_token")
@Getter
public class RegisterToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    @OneToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    public RegisterToken() {
    }

    public RegisterToken(String token, Account account) {
        this.token = token;
        this.account = account;
    }

}
