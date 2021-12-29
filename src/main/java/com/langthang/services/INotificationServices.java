package com.langthang.services;

import com.langthang.model.dto.response.NotificationDTO;
import com.langthang.model.entity.*;
import com.langthang.model.constraints.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INotificationServices {

    void addBookmarkNotification(BookmarkedPost bookmarkedPost);

    void addCommentNotification(Comment comment);

    void sendFollowersNotification(Post newPost);

    Notification createNotification(Account sourceAcc, Account destAcc, Post destPost, NotificationType type);

    List<NotificationDTO> getNotifications(String accEmail, Pageable pageable);

    List<NotificationDTO> getUnseenNotifications(String accEmail);

    void maskAsSeen(int notificationId, String accEmail);

    void maskAllAsSeen(String currentEmail);
}