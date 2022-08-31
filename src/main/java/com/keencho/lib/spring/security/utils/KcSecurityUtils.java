package com.keencho.lib.spring.security.utils;

import com.keencho.lib.spring.security.model.KcSecurityAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class KcSecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static KcSecurityAccount getKcSecurityAccount() {
        var authentication = getAuthentication();

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof KcSecurityAccount account) {
                return account;
            }
        }

        return null;
    }
}
