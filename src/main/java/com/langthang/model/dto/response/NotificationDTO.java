package com.langthang.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Notification;
import com.langthang.model.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO implements Serializable {

    private int notificationId;

    /**
     * Who will receive this notification
     */
    private String destEmail;

    /**
     * Who cause this notification
     */
    private AccountDTO sourceAccount;

    /**
     * Relative post to this notification
     */
    private PostResponseDTO destPost;

    private String content;

    private Instant notifyDate;

    private boolean seen;

    /**
     * What kind of this notification
     */
    private NotificationType notificationType;

    public static NotificationDTO toNotificationDTO(Notification notification) {
        Account sourceAcc = notification.getSourceAccount();
        Post destPost = notification.getPost();

        AccountDTO sourceAccDTO = AccountDTO.toBasicAccount(sourceAcc);

        PostResponseDTO targetPostResponseDTO = PostResponseDTO.builder()
                .postId(destPost.getId())
                .title(destPost.getTitle())
                .slug(destPost.getSlug())
                .build();

        return NotificationDTO.builder()
                .notificationId(notification.getId())
                .destEmail(notification.getAccount().getEmail())
                .sourceAccount(sourceAccDTO)
                .destPost(targetPostResponseDTO)
                .content(notification.getContent())
                .notifyDate(notification.getNotifyDate())
                .seen(notification.isSeen())
                .build();
    }
}