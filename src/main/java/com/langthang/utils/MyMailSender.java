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

    public void sendRegisterTokenEmail(String endpoint, String verifyToken, Account account) {
        String subject = "Registration Confirmation";
        String url = endpoint + verifyToken;
        String message = "Please follow this link to verify your account!"
                + "\r\n"
                + url;

        sendEmail(account.getEmail(), subject, message);
    }

    public void sendResetPasswordEmail(String endpoint, String token, Account acc) {
        String subject = "Reset Password";

        String url = endpoint + token;
        String message = "Please follow this link to reset your password"
                + "\r\n"
                + url;

        sendEmail(acc.getEmail(), subject, message);
    }

    public void sendCreatedAccountEmail(Account acc, String rawPassword) {
        String subject = "Login with Google";

        String message = "We have created a account for you based on your google public info \r\n"
                + "Please use this account to login and changed your password:\r\n"
                + "- Email: " + acc.getEmail() + "\r\n"
                + "- Password: " + rawPassword;

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
