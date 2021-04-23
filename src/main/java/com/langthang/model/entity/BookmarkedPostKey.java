package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkedPostKey implements Serializable {
    private int accountId;
    private int postId;
}
