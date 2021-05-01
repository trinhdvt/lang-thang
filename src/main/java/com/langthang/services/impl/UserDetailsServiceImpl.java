package com.langthang.services.impl;

import com.langthang.model.entity.Account;
import com.langthang.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
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
        Account acc = accRepo.findByEmailAndStatus(email, true);

        if (acc == null) {
            throw new UsernameNotFoundException("User " + email + " not found!");
        }

        return User.withUsername(email)
                .password(acc.getPassword())
                .authorities(acc.getRole())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
