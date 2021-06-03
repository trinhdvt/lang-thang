package com.langthang.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langthang.dto.CommentDTO;
import com.langthang.dto.NotificationDTO;
import com.langthang.event.*;
import com.langthang.model.Account;
import com.langthang.services.IAuthServices;
import com.langthang.utils.MyMailSender;
import com.langthang.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
@Slf4j
public class SpringEventListener {

    private final IAuthServices authServices;

    private final MyMailSender mailSender;

    private final ObjectMapper jacksonMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Value("${application.broker.notify.prefix}")
    private String onNewNotifyPrefix;

    @Value("${application.broker.post.prefix}")
    private String onNewCommentPrefix;

    @EventListener
    @Async
    public void handleConfirmRegistration(OnRegistrationEvent event) {
        Account destAccount = event.getAccount();
        String token = event.getToken();

        if (token == null) {
            token = authServices.createVerifyToken(destAccount);
        }

        mailSender.sendRegisterTokenEmail(event.getAppUrl()
                , token
                , destAccount);
    }

    @EventListener
    @Async
    public void handleResetPassword(OnResetPasswordEvent event) {
        Account destAccount = event.getAccount();
        String token = authServices.createPasswordResetToken(destAccount);

        mailSender.sendResetPasswordEmail(event.getAppUrl()
                , token
                , destAccount);
    }

    @EventListener
    @Async
    public void handleRegisterWithGoogle(OnRegisterWithGoogle event) {
        Account destAccount = event.getAccount();
        String rawPassword = event.getRawPassword();

        mailSender.sendCreatedAccountEmail(destAccount, rawPassword);
    }

    @EventListener
    @Async
    public void handleNewNotify(OnNewNotificationEvent event) throws JsonProcessingException {
        NotificationDTO notification = event.getNotification();

        String dest = onNewNotifyPrefix + "/" + notification.getDestEmail();

        messagingTemplate.convertAndSend(dest, jacksonMapper.writeValueAsString(notification));
    }

    @EventListener
    @Async
    public void handleNewComment(OnNewCommentEvent event) throws JsonProcessingException {
        CommentDTO newComment = event.getNewComment();
        newComment.setMyComment(newComment.getCommenter().getEmail().equals(Utils.getCurrentAccEmail()));

        String dest = onNewCommentPrefix + "/" + newComment.getPostId();

        messagingTemplate.convertAndSend(dest, jacksonMapper.writeValueAsString(newComment));
    }
}
