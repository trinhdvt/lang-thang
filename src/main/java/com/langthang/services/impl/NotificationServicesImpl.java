package com.langthang.services.impl;

import com.langthang.event.model.NotificationRequest;
import com.langthang.exception.NotFoundError;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.dto.response.NotificationDTO;
import com.langthang.model.entity.*;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.NotificationRepository;
import com.langthang.repository.NotificationTemplateRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.INotificationServices;
import com.langthang.specification.AccountSpec;
import com.langthang.specification.PostSpec;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
@Slf4j
public class NotificationServicesImpl implements INotificationServices {

    private final NotificationRepository notifyRepo;
    private final PostRepository postRepo;
    private final AccountRepository accRepo;
    private final NotificationTemplateRepository templateRepo;

    public void createNotification(@NonNull NotificationRequest request) {
        if (request.type().equals(NotificationType.PUBLISHED_NEW_POST)) return;

        var users = accRepo.findAllById(Set.of(request.sourceUserId(), request.targetUserId()))
                .stream()
                .filter(Account::isEnabled)
                .toList();

        var sourceUser = users.stream().filter(acc -> acc.getId() == request.sourceUserId()).findFirst().orElse(null);
        var targetUser = users.stream().filter(acc -> acc.getId() == request.targetUserId()).findFirst().orElse(null);
        var targetPost = postRepo.findOne(PostSpec.isPublished(request.targetPostId())).orElse(null);
        var type = request.type();

        if (sourceUser == null || targetUser == null || targetPost == null) {
            throw new NotFoundError("Can not find source/target user or target post");
        }

        Optional.ofNullable(buildNotification(sourceUser, targetUser, targetPost, type))
                .ifPresent(entity -> {
                    var newNotification = notifyRepo.save(entity);
                    log.debug("New notification created: {}", newNotification);
                });
    }

    @Override
    @Async
    public void sendFollowersNotification(Post newPost) {
        Account author = newPost.getAuthor();

        int pageSize = 10;
        int page = 0;
        Slice<Account> followerPage;
        do {
            followerPage = accRepo.getFollowedAccount(author.getId(), PageRequest.of(page, pageSize, Sort.by(Account_.ID)));
            List<Notification> notificationList = new ArrayList<>();

            followerPage.forEach(follower -> {
                Notification notification = buildNotification(author, follower, newPost, NotificationType.PUBLISHED_NEW_POST);
                if (notification != null) notificationList.add(notification);
            });

            notifyRepo.saveAll(notificationList);
            page++;
        } while (followerPage.hasNext());
    }

    @Override
    @Nullable
    public Notification buildNotification(@NonNull Account sourceUser,
                                          @NonNull Account targetUser,
                                          @NonNull Post targetPost,
                                          NotificationType type) {

        if (sourceUser.getId().equals(targetUser.getId())) return null;

        var templateString = getTemplate(type);
        var valuesForContent = Map.of(
                "source_user", sourceUser.getName(),
                "target_post", targetPost.getTitle()
        );

        String content = new StringSubstitutor(valuesForContent).replace(templateString);
        return new Notification(targetUser, targetPost, sourceUser, content, type);
    }

    @Override
    public List<NotificationDTO> getNotifications(String accEmail, Pageable pageable) {
        return notifyRepo.findAllByAccount_Email(accEmail, pageable)
                .map(NotificationDTO::toNotificationDTO)
                .getContent();
    }

    @Override
    public List<NotificationDTO> getUnseenNotifications(String accEmail) {
        return notifyRepo.findAllByAccount_EmailAndSeenIsFalse(accEmail,
                        Sort.by(Direction.DESC, Notification_.NOTIFY_DATE))
                .map(NotificationDTO::toNotificationDTO)
                .toList();
    }

    @Override
    public void maskAsSeen(int notificationId, String accEmail) {
        notifyRepo.findById(notificationId)
                .ifPresentOrElse(notification -> {
                    if (!notification.getAccount().getEmail().equals(accEmail))
                        throw new NotFoundError(Notification.class);

                    notification.setSeen(true);
                    notifyRepo.save(notification);
                }, () -> {
                    throw new NotFoundError(Notification.class);
                });
    }

    @Override
    public void maskAllAsSeen(String accEmail) {
        accRepo.findOne(AccountSpec.hasEmail(accEmail))
                .ifPresent(account -> notifyRepo.maskAllAsSeen(account.getId()));
    }

    @Cacheable(value = "notification-template")
    @NonNull
    public String getTemplate(NotificationType type) {
        return templateRepo.findByType(type).getTemplate();
    }
}
