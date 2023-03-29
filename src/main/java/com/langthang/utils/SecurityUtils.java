package com.langthang.utils;

import com.langthang.exception.UnauthorizedError;
import com.langthang.model.entity.Account;
import com.langthang.security.services.CurrentUser;
import lombok.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getLoggedInEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return authentication.getName();
        }
    }

    public static boolean isLoggedIn() {
        return getLoggedInEmail() != null;
    }

    public static Optional<CurrentUser> currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isLoggedIn()) return Optional.ofNullable((CurrentUser) authentication.getPrincipal());
        else return Optional.empty();
    }

    /**
     * @return Authenticated {@link Account} if user is logged in, otherwise throw {@link UnauthorizedError}
     */
    @NonNull
    public static Account authenticatedUser() {
        return currentUser()
                .map(CurrentUser::getSource)
                .orElseThrow(() -> new UnauthorizedError("You are not authorized to perform this action"));
    }
}
