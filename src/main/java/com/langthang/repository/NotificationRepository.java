package com.langthang.repository;

import com.langthang.model.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Stream<Notification> findAllByAccount_Id(Integer userId, Pageable pageable);

    Stream<Notification> findAllByAccount_IdAndSeenIs(Integer userId, boolean seen, Pageable pageable);

    @Query("update Notification nt " +
            "set nt.seen=true " +
            "where nt.account.id = ?1")
    @Modifying
    void maskAllAsSeen(int accountId);
}