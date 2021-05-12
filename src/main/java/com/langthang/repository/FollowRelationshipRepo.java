package com.langthang.repository;

import com.langthang.model.entity.FollowingRelationship;
import com.langthang.model.entity.FollowingRelationshipKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface FollowRelationshipRepo extends JpaRepository<FollowingRelationship, FollowingRelationshipKey> {
    boolean existsByAccount_IdAndFollowingAccountId(int accountId, int followingAccountId);

    @Modifying
    @Transactional
    void deleteByAccount_IdAndFollowingAccountId(int accountId, int followingAccountId);
}


