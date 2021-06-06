package com.langthang.services.impl;

import com.langthang.dto.NotificationDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.*;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.NotificationRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.INotificationServices;
import com.langthang.utils.constraints.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class NotificationServicesImpl implements INotificationServices {

    private final NotificationRepository notifyRepo;

    private final AccountRepository accRepo;

    private final PostRepository postRepo;

    @Value("${application.notify-template.like-comment}")
    private String likeNotificationTemplate;

    @Value("${application.notify-template.comment-post}")
    private String commentNotificationTemplate;

    @Value("${application.notify-template.bookmark-post}")
    private String bookmarkNotificationTemplate;

    @Value("${application.notify-template.following-new-post}")
    private String newPostNotificationTemplate;

    @Override
    @Async
    public void addBookmarkNotification(BookmarkedPost bookmarkedPost) {
        Account sourceAccount = bookmarkedPost.getAccount();
        Post destPost = bookmarkedPost.getPost();
        Account destAccount = destPost.getAccount();

        Notify bookmarkNotification = createNotification(sourceAccount, destAccount, destPost, NotificationType.BOOKMARK);
//       cannot create self-notification
        if (bookmarkNotification != null) {
            notifyRepo.saveAndFlush(bookmarkNotification);
        }
    }

    @Override
    @Async
    public void addCommentNotification(Comment comment) {
        Account sourceAccount = comment.getAccount();
        Post destPost = comment.getPost();
        Account destAccount = destPost.getAccount();

        Notify commentNotification = createNotification(sourceAccount, destAccount, destPost, NotificationType.COMMENT);
//      cannot create self-notification
        if (commentNotification != null) {
            notifyRepo.saveAndFlush(commentNotification);
        }
    }

    @Override
    public Notify createNotification(Account sourceAcc, Account destAcc, Post destPost, NotificationType type) {

        if (sourceAcc == null || destPost == null || destAcc == null) {
            throw new CustomException("Internal Server Error when create notifications", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (sourceAcc.getEmail().equals(destAcc.getEmail())) {
            return null;
        }

        String content = createContentByType(type, sourceAcc.getName(), destPost.getTitle());

        return new Notify(destAcc, destPost, sourceAcc, content);
    }

    @Override
    public Notify createNotification(int sourceAccId, int destAccId, int destPostId, NotificationType type) {
        Account sourceAcc = accRepo.findAccountByIdAndEnabled(sourceAccId, true);
        Account destAcc = accRepo.findAccountByIdAndEnabled(destAccId, true);
        Post destPost = postRepo.findPostById(destPostId);

        return createNotification(sourceAcc, destAcc, destPost, type);
    }

    @Override
    public List<NotificationDTO> getNotifications(String accEmail, Pageable pageable) {
        Page<Notify> notifyList = notifyRepo.findAllByAccount_Email(accEmail, pageable);

        return notifyList.map(NotificationDTO::toNotificationDTO).getContent();
    }

    @Override
    public List<NotificationDTO> getUnseenNotifications(String accEmail) {
        List<Notify> unseenList = notifyRepo.findAllByAccount_EmailAndSeenIsFalse(accEmail);

        return unseenList.stream().map(NotificationDTO::toNotificationDTO).collect(Collectors.toList());
    }

    @Override
    public void maskAsSeen(int notificationId, String accEmail) {
        Notify notify = notifyRepo.findById(notificationId).orElse(null);

        if (notify == null) {
            throw new CustomException("Not found!", HttpStatus.NOT_FOUND);
        }
        if (!notify.getAccount().getEmail().equals(accEmail)) {
            throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        notify.setSeen(true);
        notifyRepo.save(notify);
    }

    private String createContentByType(NotificationType type, String sourceName, String postTitle) {
        String notificationTemplate = getNotificationTemplate(type);

        return MessageFormat.format(notificationTemplate, sourceName, postTitle);
    }

    private String getNotificationTemplate(NotificationType notificationType) {
        switch (notificationType) {
            case LIKE:
                return likeNotificationTemplate;
            case COMMENT:
                return commentNotificationTemplate;
            case BOOKMARK:
                return bookmarkNotificationTemplate;
            case NEW_POST:
                return newPostNotificationTemplate;
            default:
                throw new CustomException("Type: " + notificationType + " not support"
                        , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
