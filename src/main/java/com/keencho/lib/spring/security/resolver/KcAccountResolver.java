package com.keencho.lib.spring.security.resolver;

import com.keencho.lib.spring.security.model.KcSecurityAccount;

public interface KcAccountResolver<T> {
    T getAccountBySecurityAccount(KcSecurityAccount securityAccount);
}
