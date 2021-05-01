package com.langthang.repository;

import com.langthang.model.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByEmail(String email);

    Account findByEmailAndStatus(String email, boolean status);
}
