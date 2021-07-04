package com.langthang.services;

import com.langthang.dto.AccountRegisterDTO;
import com.langthang.model.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAuthServices {

    String login(String email, String password, HttpServletResponse resp);

    String loginWithGoogle(String googleToken, HttpServletResponse resp);

    String refreshToken(String clientToken, HttpServletRequest req, HttpServletResponse resp);

    Account registerNewAccount(AccountRegisterDTO accountRegisterDTO);

    String createRegistrationToken(Account account);

    void validateRegisterToken(String token);

    String createPasswordResetToken(String email);

    void validatePasswordResetToken(String token);

    Account getAccountAndRemovePwdToken(String token);

    void updatePasswordAndSave(Account account, String newPassword);
}
