package com.langthang.services;


import com.langthang.exception.CustomException;
import com.langthang.model.entity.RefreshToken;
import com.langthang.model.entity.Role;
import com.langthang.repository.RefreshTokenRepository;
import com.langthang.services.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Component
@Slf4j
public class JwtTokenServices {

    @Value("${security.jwt.token.prefix}")
    private String TOKEN_PREFIX;

    @Value("${security.jwt.token.secret-key}")
    private String SECRET_KEY;

    @Value("${security.jwt.token.expire-length}")
    private int TOKEN_EXPIRE_TIME;

    private final UserDetailsServiceImpl userDetailsService;

    private final RefreshTokenRepository refreshTokenRepo;

    @Autowired
    public JwtTokenServices(UserDetailsServiceImpl userDetailsService, RefreshTokenRepository refreshTokenRepo) {
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    public String createToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", new SimpleGrantedAuthority(role.getAuthority()));

        Date now = new Date();
        Date expireTime = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String username = getUserName(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String createRefreshToken(String email) {
        byte[] rfToken = new byte[10];
        new Random().nextBytes(rfToken);
        String refreshToken = Base64.getEncoder().encodeToString(rfToken);

        RefreshToken currentToken = refreshTokenRepo.findByEmail(email);
        if (currentToken == null) {
            currentToken = new RefreshToken(email, refreshToken);
        } else {
            currentToken.setToken(refreshToken);
        }
        refreshTokenRepo.save(currentToken);
        return refreshToken;
    }

    public void addRefreshTokenCookie(String email, HttpServletResponse resp) {
        String refreshToken = createRefreshToken(email);

        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(TOKEN_EXPIRE_TIME / 1000); // ms -> s
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public boolean isValidRefreshToken(String email, String clientToken) {
        RefreshToken rfTokenInDB = refreshTokenRepo.findByEmail(email);
        return (rfTokenInDB != null && rfTokenInDB.getToken().equals(clientToken));
    }

    public String getUserName(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX + " ")) {
            return bearerToken.replace(TOKEN_PREFIX + " ", "");
        }
        return null;
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Invalid or expired JWT Token", HttpStatus.FORBIDDEN);
        }
    }

}
