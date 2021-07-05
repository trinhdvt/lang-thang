package com.langthang.services;


import com.langthang.exception.CustomException;
import com.langthang.model.RefreshToken;
import com.langthang.repository.RefreshTokenRepository;
import com.langthang.services.impl.UserDetailsServiceImpl;
import com.langthang.utils.Utils;
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
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Transactional
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

    public String createRefreshToken(String email, String accessToken) {
        String refreshToken = Utils.randomString(10);

        RefreshToken rf = refreshTokenRepo.findByEmail(email);

        if (rf == null) {
            rf = new RefreshToken(email, refreshToken, accessToken);
        } else {
            rf.setRefreshToken(refreshToken);
            rf.setAccessToken(accessToken);
        }

        return refreshTokenRepo.saveAndFlush(rf).getRefreshToken();
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
