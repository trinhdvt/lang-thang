package com.langthang.utils;

import com.langthang.security.services.CurrentUser;
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

}
