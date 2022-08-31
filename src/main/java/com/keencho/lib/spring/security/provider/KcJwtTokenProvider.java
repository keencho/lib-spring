package com.keencho.lib.spring.security.provider;

import com.keencho.lib.spring.security.model.KcSecurityAccount;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface KcJwtTokenProvider {

    long getExpireDays();

    @Transactional(readOnly = true)
    Authentication getAuthentication(String token);

    String resolveToken(HttpServletRequest request);

    String createToken(KcSecurityAccount securityAccount);

    boolean isValidate(String jwtToken);

    String getCookieName();
}
