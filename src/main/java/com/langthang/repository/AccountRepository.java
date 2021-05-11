package com.langthang.repository;

import com.langthang.model.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByEmail(String email);

    Account findByEmailAndEnabled(String email, boolean enabled);

    @Query("select count(a) from FollowingRelationship a where a.accountId=?1")
    int countFollowing(int accountId);
}
