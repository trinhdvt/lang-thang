package com.langthang.security.services;

import com.langthang.model.entity.Account;
import com.langthang.repository.AccountRepository;
import com.langthang.specification.AccountSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accRepo;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accRepo) {
        this.accRepo = accRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account acc = accRepo.findOne(AccountSpec.hasEmail(email)).
                orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + email));

        return new CurrentUser(acc.getEmail(),
                acc.getPassword(),
                acc.isEnabled(),
                acc.getId(),
                acc.getRole(),
                acc);
    }
}