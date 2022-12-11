package com.langthang.services.impl;

import com.langthang.model.dto.response.NotificationDTO;
import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.entity.*;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.NotificationRepository;
import com.langthang.services.INotificationServices;
import com.langthang.utils.AssertUtils;
import com.langthang.model.constraints.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class NotificationServicesImpl implements INotificationServices {

    private final NotificationRepository notifyRepo;
    private final AccountRepository accRepo;
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

        Notification bookmarkNotification = createNotification(sourceAccount, destAccount, destPost, NotificationType.BOOKMARK);
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

        Notification commentNotification = createNotification(sourceAccount, destAccount, destPost, NotificationType.COMMENT);
//      cannot create self-notification
        if (commentNotification != null) {
            notifyRepo.saveAndFlush(commentNotification);
        }
    }

    @Override
    @Async
    public void sendFollowersNotification(Post newPost) {
        Account author = newPost.getAccount();

        int pageSize = 100;
        int page = 0;
        Slice<Account> followerPage;
        do {
            followerPage = accRepo.getFollowedAccount(author.getId(), PageRequest.of(page, pageSize, Sort.by("id")));
            List<Notification> notificationList = new ArrayList<>();

            followerPage.forEach(follower -> {
                Notification notification = createNotification(author, follower, newPost, NotificationType.NEW_POST);
                if (notification != null)
                    notificationList.add(notification);
            });

            notifyRepo.saveAll(notificationList);
            page++;
        } while (followerPage.hasNext());

    }

    @Override
    public Notification createNotification(Account sourceAcc, Account destAcc, Post destPost, NotificationType type) {

        if (sourceAcc == null || destPost == null || destAcc == null) {
            throw new HttpError("Internal Server Error when create notifications", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (sourceAcc.getEmail().equals(destAcc.getEmail())) {
            return null;
        }

        String content = createContentByType(type, sourceAcc.getName(), destPost.getTitle());

        return new Notification(destAcc, destPost, sourceAcc, content);
    }

    @Override
    public List<NotificationDTO> getNotifications(String accEmail, Pageable pageable) {
        Page<Notification> notifyList = notifyRepo.findAllByAccount_Email(accEmail, pageable);

        return notifyList.map(NotificationDTO::toNotificationDTO).getContent();
    }

    @Override
    public List<NotificationDTO> getUnseenNotifications(String accEmail) {
        List<Notification> unseenList = notifyRepo.findAllByAccount_EmailAndSeenIsFalse(accEmail,
                Sort.by(Sort.Direction.DESC, "notifyDate"));

        return unseenList.stream().map(NotificationDTO::toNotificationDTO).collect(Collectors.toList());
    }

    @Override
    public void maskAsSeen(int notificationId, String accEmail) {
        Notification notification = notifyRepo.findById(notificationId).orElse(null);
        AssertUtils.notNull(notification, new NotFoundError("Notification not found"));
        AssertUtils.isTrue(notification.getAccount().getEmail().equals(accEmail), new UnauthorizedError("Notification not found"));

        notification.setSeen(true);
        notifyRepo.save(notification);
    }

    @Override
    public void maskAllAsSeen(String accEmail) {
        Account acc = accRepo.findAccountByEmail(accEmail);

        notifyRepo.maskAllAsSeen(acc.getId());
    }

    private String createContentByType(NotificationType type, String sourceName, String postTitle) {
        String notificationTemplate = getNotificationTemplate(type);

        return MessageFormat.format(notificationTemplate, sourceName, postTitle);
    }

    private String getNotificationTemplate(NotificationType notificationType) {
        return switch (notificationType) {
            case LIKE -> likeNotificationTemplate;
            case COMMENT -> commentNotificationTemplate;
            case BOOKMARK -> bookmarkNotificationTemplate;
            case NEW_POST -> newPostNotificationTemplate;
        };
    }
}