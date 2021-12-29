package com.langthang.repository;

import com.langthang.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    RefreshToken findByEmail(String email);
}