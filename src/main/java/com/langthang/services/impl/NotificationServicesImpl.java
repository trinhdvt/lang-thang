package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.NotificationDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.event.OnNewNotificationEvent;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Notify;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.NotificationRepository;
import com.langthang.services.INotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@Transactional
public class NotificationServicesImpl implements INotificationServices {

    @Autowired
    private NotificationRepository notifyRepo;

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${application.notify-template.like-comment}")
    private String likeNotificationTemplate;

    @Value("${application.notify-template.comment-post}")
    private String commentNotificationTemplate;

    @Value("${application.notify-template.bookmark-post}")
    private String bookmarkNotificationTemplate;

    @Value("${application.notify-template.following-new-post}")
    private String newPostNotificationTemplate;

    @Override
    public void createNotification(Account sourceAcc, Account destAcc, Post destPost, NotificationDTO.TYPE type) {
        if (sourceAcc == null || destAcc == null || destPost == null) {
            throw new CustomException("Internal Server Error when create notifications", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (sourceAcc.getEmail().equals(destAcc.getEmail())) {
            return;
        }

        String content = createContentByType(type, sourceAcc.getName(), destPost.getTitle());
        Notify newNotify = new Notify(destAcc, destPost, sourceAcc, content);
        newNotify = notifyRepo.save(newNotify);

        NotificationDTO notificationDTO = toNotificationDTO(newNotify);
        notificationDTO.setNotificationType(type);

        eventPublisher.publishEvent(new OnNewNotificationEvent(notificationDTO));
    }

    @Override
    @Async
    public void createNotification(Account sourceAcc, String targetAccEmail, Post destPost, NotificationDTO.TYPE type) {
        Account targetAcc = accRepo.findAccountByEmailAndEnabled(targetAccEmail, true);
        createNotification(sourceAcc, targetAcc, destPost, type);
    }

    @Override
    public List<NotificationDTO> getNotifications(String accEmail, Pageable pageable) {
        Page<Notify> notifyList = notifyRepo.findAllByAccount_Email(accEmail, pageable);

        return notifyList.map(this::toNotificationDTO).getContent();
    }

    @Override
    public List<NotificationDTO> getUnseenNotifications(String accEmail, Pageable pageable) {
        Page<Notify> unseenList = notifyRepo.findAllByAccount_EmailAndSeenIsFalse(accEmail, pageable);

        return unseenList.map(this::toNotificationDTO).getContent();
    }

    @Override
    public void maskAsSeen(int notificationId, String accEmail) {
        Notify notify = notifyRepo.findById(notificationId).orElse(null);

        if (notify == null) {
            return;
        }
        if (!notify.getAccount().getEmail().equals(accEmail)) {
            throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        notify.setSeen(true);
        notifyRepo.save(notify);
    }

    private NotificationDTO toNotificationDTO(Notify notify) {
        Account sourceAcc = notify.getSourceAccount();
        Post destPost = notify.getPost();

        AccountDTO sourceAccDTO = AccountDTO.toBasicAccount(sourceAcc);

        PostResponseDTO targetPostResponseDTO = PostResponseDTO.builder()
                .postId(destPost.getId())
                .title(destPost.getTitle())
                .slug(destPost.getSlug())
                .build();

        return NotificationDTO.builder()
                .notificationId(notify.getId())
                .destEmail(notify.getAccount().getEmail())
                .sourceAccount(sourceAccDTO)
                .destPost(targetPostResponseDTO)
                .content(notify.getContent())
                .notifyDate(notify.getNotifyDate())
                .seen(notify.isSeen())
                .build();
    }

    private String createContentByType(NotificationDTO.TYPE type, String sourceName, String postTitle) {
        String notificationTemplate = getNotificationTemplate(type);

        return MessageFormat.format(notificationTemplate, sourceName, postTitle);
    }

    private String getNotificationTemplate(NotificationDTO.TYPE notificationType) {
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