package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "external_account")
public class ExternalAccount {
    @Id
    @Column(name = "account_id")
    private Integer accountId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Account account;

    private String fbLink;
    private String instagramLink;
}
