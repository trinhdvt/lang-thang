package com.langthang.services;

import com.langthang.dto.AccountRegisterDTO;
import com.langthang.model.Account;
import com.langthang.model.RegisterToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAuthServices {
    String login(String email, String password, HttpServletResponse resp);

    String refreshToken(String clientToken, HttpServletRequest req, HttpServletResponse resp);

    Account registerNewAccount(AccountRegisterDTO accountRegisterDTO);

    String createVerifyToken(Account account);

    RegisterToken generateNewRegisterToken(String existToken);

    void validateRegisterToken(String token);

    Account findAccountByEmail(String email);

    String createPasswordResetToken(Account account);

    void validatePasswordResetToken(String token);

    Account findAccountByPasswordResetToken(String token);

    void changeAccountPassword(Account account, String newPassword);

    Account saveCreatedGoogleAccount(Account tmpAcc);

    Account createAccountUseGoogleToken(String googleIdToken);
}
