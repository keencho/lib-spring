package com.keencho.lib.spring.security.provider;

import com.keencho.lib.spring.security.model.KcSecurityAccount;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.Jwts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public abstract class KcDefaultJwtTokenProvider implements KcJwtTokenProvider {

    private final String secretKey;
    private final UserDetailsService userDetailsService;

    public KcDefaultJwtTokenProvider(String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.userDetailsService = userDetailsService;
    }

    public abstract int expireDays();

    @Override
    public Authentication getAuthentication(String token) {
        return null;
    }

    @Override
    public String createToken(KcSecurityAccount<?> securityAccount) {
        var claims = Jwts.claims().setSubject(UUID.randomUUID().toString());

        claims.put("data", securityAccount.getData());

        var limit = LocalDateTime.now().plusDays(this.expireDays());
        var date = new Date();

        return Jwts.builder().setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(Date.from(limit.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, this.secretKey)
                .compact();
    }
}
