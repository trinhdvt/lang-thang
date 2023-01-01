package com.langthang.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookmarkedPostKey implements Serializable {
    @Column(name = "account_id")
    private int accountId;

    @Column(name = "post_id")
    private int postId;
}