package com.keencho.lib.spring.security.resolver;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.model.KcSecurityAccount;

public interface KcAccountResolver<T extends KcAccountBaseModel> {
    T getAccountBySecurityAccount(KcSecurityAccount securityAccount);
}
