package com.langthang.repository;

import com.langthang.model.entity.Notify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notify, Integer> {

    Page<Notify> findAllByAccount_Email(String accountEmail, Pageable pageable);

    Page<Notify> findAllByAccount_EmailAndSeenIsFalse(String accountEmail, Pageable pageable);

}
