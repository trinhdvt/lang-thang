package com.langthang.repository;

import com.langthang.model.Notify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notify, Integer> {

    Page<Notify> findAllByAccount_Email(String accountEmail, Pageable pageable);

    List<Notify> findAllByAccount_EmailAndSeenIsFalse(String accountEmail);

}
