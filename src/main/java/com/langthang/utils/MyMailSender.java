package com.langthang.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MyMailSender {

    private final JavaMailSender mailSender;

    @Autowired
    public MyMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRegisterTokenEmail(String destEmail, String confirmUrl) {
        String subject = "Registration Confirmation";
        String message = "Please follow this link to verify your account!"
                + "\r\n"
                + confirmUrl;

        sendEmail(destEmail, subject, message);
    }

    @Async
    public void sendResetPasswordEmail(String destEmail, String confirmUrl) {
        String subject = "Reset Password";
        String message = "Please follow this link to reset your password"
                + "\r\n"
                + confirmUrl;

        sendEmail(destEmail, subject, message);
    }

    @Async
    public void sendCreatedAccountEmail(String destEmail, String rawPassword) {
        String subject = "Login with Google";

        String message = "We have created a account for you based on your google public info \r\n"
                + "Please use this account to login and changed your password:\r\n"
                + "- Email: " + destEmail+ "\r\n"
                + "- Password: " + rawPassword;

        sendEmail(destEmail, subject, message);
    }

    private void sendEmail(String recipient, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
