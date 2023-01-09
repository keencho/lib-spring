package com.keencho.lib.spring.security.provider;

import com.keencho.lib.spring.security.model.KcSecurityAccount;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface KcJwtTokenProvider {

    long getExpireDays();

    @Transactional(readOnly = true)
    Authentication getAuthentication(String token);

    String resolveToken(HttpServletRequest request);

    String createToken(KcSecurityAccount securityAccount);

    boolean isValidate(String jwtToken);

    String getCookieName();
}
