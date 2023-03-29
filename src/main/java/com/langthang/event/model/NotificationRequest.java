package com.langthang.event.model;

import com.langthang.model.constraints.NotificationType;

import java.io.Serializable;

public record NotificationRequest(
        int sourceUserId,
        int targetUserId,
        int targetPostId,
        NotificationType type
) implements Serializable {
}
