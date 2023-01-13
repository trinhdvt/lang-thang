package com.langthang.event.listener;

import com.langthang.model.entity.BookmarkedPost;
import com.langthang.services.INotificationServices;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BookmarkEntityListener {

    private static INotificationServices notificationServices;

    @Autowired
    public void init(INotificationServices notificationServices) {
        BookmarkEntityListener.notificationServices = notificationServices;
    }


    @PostPersist
    public void onNewBookmark(BookmarkedPost bookmarkedPost) {
//        notificationServices.addBookmarkNotification(bookmarkedPost);
    }
}