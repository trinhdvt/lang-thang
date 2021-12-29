package com.langthang.services;

import com.langthang.model.dto.request.AccountRegisterDTO;
import com.langthang.model.dto.request.PasswordDTO;

import javax.servlet.http.HttpServletResponse;

public interface IAuthServices {

    String login(String email, String password, HttpServletResponse resp);

    String loginWithGoogle(String googleToken, HttpServletResponse resp);

    String reCreateToken(String clientToken, String accessToken, HttpServletResponse resp);

    void registerAccount(AccountRegisterDTO accountRegisterDTO);

    void validateRegisterToken(String token);

    void createPasswordResetToken(String email);

    void validatePasswordResetToken(String token);

    void resetPassword(String token, PasswordDTO passwordDTO);

}