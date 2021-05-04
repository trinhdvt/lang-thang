package com.langthang.repository;

import com.langthang.model.entity.RegisterToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterTokenRepository extends JpaRepository<RegisterToken, Integer> {
    RegisterToken findByToken(String token);
}
