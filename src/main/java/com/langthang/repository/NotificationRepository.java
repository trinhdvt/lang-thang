package com.langthang.repository;

import com.langthang.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Stream;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findAllByAccount_Email(String accountEmail, Pageable pageable);

    Stream<Notification> findAllByAccount_EmailAndSeenIsFalse(String accountEmail, Sort sort);

    @Query("update Notification nt " +
            "set nt.seen=true " +
            "where nt.account.id = ?1")
    @Modifying
    void maskAllAsSeen(int accountId);
}