package com.langthang.services;

import com.langthang.dto.AccountRegisterDTO;
import com.langthang.model.Account;

import javax.servlet.http.HttpServletResponse;

public interface IAuthServices {

    String login(String email, String password, HttpServletResponse resp);

    String loginWithGoogle(String googleToken, HttpServletResponse resp);

    String reCreateToken(String clientToken,  String accessToken, HttpServletResponse resp);

    void registerAccount(AccountRegisterDTO accountRegisterDTO);

    void validateRegisterToken(String token);

    String createPasswordResetToken(String email);

    void validatePasswordResetToken(String token);

    Account getAccountAndRemovePwdToken(String token);

    void updatePasswordAndSave(Account account, String newPassword);
}
