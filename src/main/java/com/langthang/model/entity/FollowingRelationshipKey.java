package com.langthang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FollowingRelationshipKey implements Serializable {
    private Integer accountId;
    private Integer followingAccountId;
}
