package com.langthang.repository;

import com.langthang.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByEmail(String email);

    Account findAccountByEmailAndEnabled(String email, boolean enabled);

    Account findAccountByIdAndEnabled(int accountId, boolean enabled);

    @Query("select count(a) from FollowingRelationship a where a.followingAccountId=?1")
    int countFollowing(int accountId);

    @Query("select count(bp) " +
            "from BookmarkedPost bp join Post p on bp.post.id = p.id " +
            "where p.account.id = ?1")
    int countBookmarkOnMyPost(int accountId);

    @Query("select count(c.id) " +
            "from Comment c join Post p on c.post.id = p.id " +
            "where p.account.id = ?1")
    int countCommentOnMyPost(int accountId);
}
