package com.langthang.services;

import com.langthang.dto.NotificationDTO;
import com.langthang.model.*;
import com.langthang.utils.constraints.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INotificationServices {

    void addBookmarkNotification(BookmarkedPost bookmarkedPost);

    void addCommentNotification(Comment comment);

    void sendFollowersNotification(Post newPost);

    Notify createNotification(Account sourceAcc, Account destAcc, Post destPost, NotificationType type);

    List<NotificationDTO> getNotifications(String accEmail, Pageable pageable);

    List<NotificationDTO> getUnseenNotifications(String accEmail);

    void maskAsSeen(int notificationId, String accEmail);
}
