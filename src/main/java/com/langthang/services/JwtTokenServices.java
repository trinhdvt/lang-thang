package com.langthang.services;


import com.langthang.exception.CustomException;
import com.langthang.model.entity.RefreshToken;
import com.langthang.repository.RefreshTokenRepository;
import com.langthang.services.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Component
@Transactional
public class JwtTokenServices {

    @Value("${security.jwt.token.prefix}")
    private String TOKEN_PREFIX;

    @Value("${security.jwt.token.secret-key}")
    private String SECRET_KEY;

    @Value("${security.jwt.token.expire-length}")
    private int TOKEN_EXPIRE_TIME;

    @Value("${security.jwt.refresh-token.cookie-name}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    @Value("${security.jwt.refresh-token.cookie-length}")
    private int REFRESH_TOKEN_COOKIE_LENGTH;

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

    public String createAccessToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);

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

    private String createRefreshToken() {
        byte[] rfToken = new byte[10];
        new Random().nextBytes(rfToken);

        return Base64.getEncoder().encodeToString(rfToken);
    }

    public void saveRefreshToken(String email, String accessToken, String refreshToken) {
        RefreshToken rf = refreshTokenRepo.findByEmail(email);

        if (rf == null) {
            rf = new RefreshToken(email, refreshToken, accessToken);
        } else {
            rf.setRefreshToken(refreshToken);
            rf.setAccessToken(accessToken);
        }

        refreshTokenRepo.saveAndFlush(rf);
    }

    public void addRefreshTokenCookie(String email, String accessToken, HttpServletResponse resp) {
        String refreshToken = createRefreshToken();

        saveRefreshToken(email, accessToken, refreshToken);

        // add refresh token cookie to response
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_LENGTH); // ms -> s
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public boolean isValidToCreateNewAccessToken(String email, String refreshToken, String accessToken) {
        RefreshToken rfTokenInDB = refreshTokenRepo.findByEmail(email);

        return (rfTokenInDB != null
                && rfTokenInDB.getRefreshToken().equals(refreshToken)
                && rfTokenInDB.getAccessToken().equals(accessToken));
    }

    public String getUserName(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public String getAccessToken(HttpServletRequest req) {
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
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Invalid JWT Token", HttpStatus.FORBIDDEN);
        }
    }

}
