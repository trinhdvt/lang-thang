package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "following_relationship")
@IdClass(FollowingRelationshipKey.class)
public class FollowingRelationship {
    @Id
    @Column(name = "account_id")
    private Integer accountId;

    @Id
    @Column(name = "following_account_id")
    private Integer followingAccountId;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId(value = "accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    private Date followingDate;
}

