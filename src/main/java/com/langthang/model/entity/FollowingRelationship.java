package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

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

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @MapsId(value = "accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "following_date")
    @CreatedDate
    private Instant followingDate;

    public FollowingRelationship(Integer accountId, Integer followingAccountId) {
        this.accountId = accountId;
        this.followingAccountId = followingAccountId;
    }
}