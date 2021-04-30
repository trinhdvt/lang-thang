package com.langthang.repository;

import com.langthang.model.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByEmail(String email);

    Account findByEmailAndStatus(String email, boolean status);
}
