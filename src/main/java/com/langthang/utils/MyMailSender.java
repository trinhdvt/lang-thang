package com.langthang.utils;

import com.langthang.model.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MyMailSender {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegisterTokenEmail(String contextPath, String verifyToken, Account account) {
        String subject = "Registration Confirmation";
        String url = contextPath + "/registrationConfirm?token=" + verifyToken;
        String message = "Please follow this link to verify your account!"
                + "\r\n"
                + url;

        sendEmail(account.getEmail(), subject, message);
    }

    public void sendResetPasswordEmail(String contextPath, String token, Account acc) {
        String subject = "Reset Password";

        String url = contextPath + "/changePassword?token=" + token;
        String message = "Please follow this link to reset your password"
                + "\r\n"
                + url;

        sendEmail(acc.getEmail(), subject, message);
    }

    private void sendEmail(String recipient, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
