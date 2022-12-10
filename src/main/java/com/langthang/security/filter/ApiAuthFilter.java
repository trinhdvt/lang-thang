package com.langthang.security.filter;

import com.langthang.exception.HttpError;
import com.langthang.security.services.TokenServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiAuthFilter extends OncePerRequestFilter {

    private final TokenServices jwtTokenServices;

    @Autowired
    public ApiAuthFilter(TokenServices jwtTokenServices) {
        this.jwtTokenServices = jwtTokenServices;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse resp,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        String token = jwtTokenServices.getAccessToken(req);

        try {
            if (token != null && jwtTokenServices.isValidToken(token)) {
                Authentication auth = jwtTokenServices.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (HttpError e) {
            SecurityContextHolder.clearContext();
            resp.sendError(e.getHttpStatus().value(), e.getMessage());
            return;
        }

        filterChain.doFilter(req, resp);
    }
}