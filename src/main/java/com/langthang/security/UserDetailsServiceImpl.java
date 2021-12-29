package com.langthang.security;

import com.langthang.model.Account;
import com.langthang.repository.AccountRepository;
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
        Account acc = accRepo.findAccountByEmail(email);

        if (acc == null) {
            throw new UsernameNotFoundException("Account with " + email + " not found!");
        }

        return new CurrentUser(acc.getEmail(),
                acc.getPassword(),
                acc.isEnabled(),
                acc.getId(),
                acc.getRole());
    }
}