package com.langthang.security;


import com.langthang.exception.HttpError;
import com.langthang.model.Account;
import com.langthang.model.RefreshToken;
import com.langthang.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class TokenServices {

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepo;
    @Value("${security.jwt.token.prefix}")
    private String TOKEN_PREFIX;
    @Value("${security.jwt.token.secret-key}")
    private String SECRET_KEY;
    @Value("${security.jwt.token.expire-length}")
    private int TOKEN_EXPIRE_TIME;

    @Autowired
    public TokenServices(UserDetailsServiceImpl userDetailsService, RefreshTokenRepository refreshTokenRepo) {
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    public String createAccessToken(Account account) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("jti", account.getId());
        payloads.put("sub", account.getEmail());
        payloads.put("role", account.getRole());
        payloads.put("name", account.getName());
        payloads.put("avatarLink", account.getAvatarLink());
        payloads.put("about", account.getAbout());
        payloads.put("fbLink", account.getFbLink());
        payloads.put("instagramLink", account.getInstagramLink());

        Date now = new Date();
        Date expireTime = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(payloads)
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
        String refreshToken = RandomStringUtils.randomAlphanumeric(10);

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
            throw new HttpError("Invalid JWT Token", HttpStatus.FORBIDDEN);
        }
    }

}