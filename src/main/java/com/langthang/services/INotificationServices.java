package com.langthang.services;

import com.langthang.event.model.NotificationRequest;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.dto.response.NotificationDTO;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Notification;
import com.langthang.model.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INotificationServices {

    /**
     * Create a new notification based on {@code notificationRequest}.
     * Do not call this if {@code notificationRequest} is {@code PUBLISHED_NEW_POST}
     *
     * @param notificationRequest contains information of notification
     */
    void createNotification(NotificationRequest notificationRequest);

    /**
     * Build a notification entity
     *
     * @param sourceUser is the user who trigger the notification
     * @param targetUser is the user who receive the notification
     * @param targetPost is the post which is related to the notification
     * @param type       is the type of notification
     * @return a new {@link Notification} entity. Null if the sourceUser and targetUser are the same
     */
    Notification buildNotification(Account sourceUser, Account targetUser, Post targetPost, NotificationType type);

    void sendFollowersNotification(Post newPost);

    List<NotificationDTO> getNotifications(String accEmail, Pageable pageable);

    List<NotificationDTO> getUnseenNotifications(String accEmail);

    void maskAsSeen(int notificationId, String accEmail);

    void maskAllAsSeen(String currentEmail);
}