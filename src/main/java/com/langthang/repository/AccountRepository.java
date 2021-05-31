package com.langthang.repository;

import com.langthang.model.Account;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findAccountByEmail(String email);

    Account findAccountByEmailAndEnabled(String email, boolean enabled);

    Account findAccountByIdAndEnabled(int accountId, boolean enabled);

    @Query("select count(a) from FollowingRelationship a where a.followingAccountId=?1")
    int countFollowing(int accountId);

    @Query("select a " +
            "from FollowingRelationship fr left join Account a " +
            "on fr.followingAccountId=a.id " +
            "group by (fr.followingAccountId) " +
            "order by count(fr.followingAccountId) DESC ")
    List<Account> getTopFollowingAccount(PageRequest pageable);

    @Query("select count(bp) " +
            "from BookmarkedPost bp join Post p on bp.post.id = p.id " +
            "where p.account.id = ?1")
    int countBookmarkOnMyPost(int accountId);

    @Query("select count(c.id) " +
            "from Comment c join Post p on c.post.id = p.id " +
            "where p.account.id = ?1")
    int countCommentOnMyPost(int accountId);

    @Query("select count(p.account) " +
            "from Post p where p.account.id=?1 and p.status=true ")
    int countPublishedPost(int accountId);

    @Query("select acc " +
            "from FollowingRelationship fr left join Account acc " +
            "on fr.accountId=acc.id " +
            "where fr.followingAccountId=?1")
    List<Account> getFollowedAccount(int accountId);
}
