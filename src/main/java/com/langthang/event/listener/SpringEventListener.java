package com.langthang.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langthang.dto.CommentDTO;
import com.langthang.dto.NotificationDTO;
import com.langthang.event.OnNewCommentEvent;
import com.langthang.event.OnNewNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
public class SpringEventListener {

    private final ObjectMapper jacksonMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Value("${application.broker.notify.prefix}")
    private String onNewNotifyPrefix;

    @Value("${application.broker.post.prefix}")
    private String onNewCommentPrefix;

    @EventListener
    @Async
    public void handleNewNotify(OnNewNotificationEvent event) throws JsonProcessingException {
        NotificationDTO notification = event.getNotification();

        String dest = onNewNotifyPrefix + "/" + notification.getDestEmail();
        log.debug("[Socket] Notification with ID {} was sent", notification.getNotificationId());
        messagingTemplate.convertAndSend(dest, jacksonMapper.writeValueAsString(notification));
    }

    @EventListener
    @Async
    public void handleNewComment(OnNewCommentEvent event) throws JsonProcessingException {
        CommentDTO newComment = event.getNewComment();
        newComment.setMyComment(false);
        String dest = onNewCommentPrefix + "/" + newComment.getPostId();

        log.debug("[Socket] Comment with ID {} was sent", newComment.getCommentId());
        messagingTemplate.convertAndSend(dest, jacksonMapper.writeValueAsString(newComment));
    }
}
