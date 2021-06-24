package com.langthang.repository;

import com.langthang.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findAllByAccount_Email(String accountEmail, Pageable pageable);

    List<Notification> findAllByAccount_EmailAndSeenIsFalse(String accountEmail);

}
