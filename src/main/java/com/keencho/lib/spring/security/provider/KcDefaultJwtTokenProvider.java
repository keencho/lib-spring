package com.keencho.lib.spring.security.provider;

import com.keencho.lib.spring.security.model.KcSecurityAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public abstract class KcDefaultJwtTokenProvider implements KcJwtTokenProvider {

    private final String secretKey;
    private final UserDetailsService userDetailsService;
    private final String claimsKeyName = "loginId";

    public KcDefaultJwtTokenProvider(String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.userDetailsService = userDetailsService;
    }

    public abstract long getExpireDays();
    public abstract String getCookieName();

    @Override
    public Authentication getAuthentication(String token) {
        var claims = this.getClaims(token);

        var loginId = claims.get(this.claimsKeyName, String.class);

        if (!StringUtils.hasText(loginId)) {
            return null;
        }

        var userDetails = userDetailsService.loadUserByUsername(loginId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie: cookies) {
                if (this.getCookieName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public String createToken(KcSecurityAccount securityAccount) {
        var claims = Jwts.claims().setSubject(UUID.randomUUID().toString());

        claims.put(this.claimsKeyName, securityAccount.getLoginId());
        claims.put("data", securityAccount.getData());

        var limit = LocalDateTime.now().plusDays(this.getExpireDays());
        var date = new Date();

        return Jwts.builder().setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(Date.from(limit.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, this.secretKey)
                .compact();
    }

    @Override
    public boolean isValidate(String jwtToken) {

        if (!StringUtils.hasText(jwtToken)) {
            return false;
        }

        try {
            var claims = this.getClaims(jwtToken);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // ExpiredJwtException, MalformedJwtException, SignatureException 이 넘어올수 있다.
            // 만료가 되었다면 아예 예외가 넘어올수 있으므로 예외처리 블록으로 묶고 false를 리턴함.
            return false;
        }
    }

    private Claims getClaims(String jwtToken) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
