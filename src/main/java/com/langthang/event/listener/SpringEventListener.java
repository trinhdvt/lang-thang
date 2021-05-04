package com.langthang.event.listener;

import com.langthang.event.OnRegistrationEvent;
import com.langthang.event.OnResetPasswordEvent;
import com.langthang.model.entity.Account;
import com.langthang.services.IAuthServices;
import com.langthang.utils.MyMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SpringEventListener {

    @Autowired
    private IAuthServices authServices;

    @Autowired
    private MyMailSender mailSender;

    @EventListener
    @Async
    public void handleConfirmRegistration(OnRegistrationEvent event) {
        Account account = event.getAccount();
        String token = event.getToken();

        if (token == null) {
            token = authServices.createVerifyToken(account);
        }

        mailSender.sendRegisterTokenEmail(event.getAppUrl()
                , token
                , account);
    }

    @EventListener
    @Async
    public void handleResetPassword(OnResetPasswordEvent event) {
        Account account = event.getAccount();
        String token = authServices.createPasswordResetToken(account);

        mailSender.sendResetPasswordEmail(event.getAppUrl()
                , token
                , account);
    }
}
