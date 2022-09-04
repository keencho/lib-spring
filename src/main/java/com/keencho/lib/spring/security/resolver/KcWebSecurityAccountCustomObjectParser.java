package com.keencho.lib.spring.security.resolver;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.model.KcSecurityAccount;

public interface KcWebSecurityAccountCustomObjectParser<T> {
    T parse(KcSecurityAccount securityAccount);
}
