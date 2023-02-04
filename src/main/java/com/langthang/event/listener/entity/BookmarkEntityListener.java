package com.langthang.event.listener.entity;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import com.langthang.config.RQueueConfig;
import com.langthang.event.model.NotificationRequest;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.entity.BookmarkedPost;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BookmarkEntityListener {

    private static RqueueMessageEnqueuer msgPublisher;

    @Autowired
    public void init(RqueueMessageEnqueuer msgPublisher) {
        BookmarkEntityListener.msgPublisher = msgPublisher;
    }

    @PostPersist
    public void onNewBookmark(BookmarkedPost bookmarkedPost) {
        var notificationRequest = new NotificationRequest(
                bookmarkedPost.getAccount().getId(),
                bookmarkedPost.getPost().getAuthor().getId(),
                bookmarkedPost.getPost().getId(),
                NotificationType.BOOKMARK_POST
        );
        msgPublisher.enqueue(RQueueConfig.QK_NOTIFICATION_FACTORY_QUEUE, notificationRequest);
    }
}