package com.langthang.security;

import com.langthang.exception.CustomException;
import com.langthang.services.JwtTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenServices jwtTokenServices;

    @Autowired
    public JwtTokenFilter(JwtTokenServices jwtTokenServices) {
        this.jwtTokenServices = jwtTokenServices;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenServices.getAccessToken(req);

        try {
            if (token != null && jwtTokenServices.isValidToken(token)) {
                Authentication auth = jwtTokenServices.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            resp.sendError(e.getHttpStatus().value(), e.getMessage());
            return;
        }

        filterChain.doFilter(req, resp);
    }
}
