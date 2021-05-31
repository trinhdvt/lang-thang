package com.langthang.repository;

import com.langthang.model.RegisterToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterTokenRepository extends JpaRepository<RegisterToken, Integer> {
    RegisterToken findByToken(String token);
}
