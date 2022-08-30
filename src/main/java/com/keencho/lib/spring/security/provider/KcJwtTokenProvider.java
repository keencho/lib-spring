package com.keencho.lib.spring.security.provider;

import com.keencho.lib.spring.security.model.KcSecurityAccount;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface KcJwtTokenProvider {

    int expireDays();

    @Transactional(readOnly = true)
    Authentication getAuthentication(String token);

    String createToken(KcSecurityAccount<?> securityAccount);
}
