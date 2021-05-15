package com.langthang.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class NotificationDTO {

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

    private Date notifyDate;

    private boolean seen;

    /**
     * What kind of this notification
     */
    private TYPE notificationType;

    public enum TYPE {
        LIKE, COMMENT, REPORT, BOOKMARK, NEW_POST
    }
}
