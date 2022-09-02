package com.keencho.lib.spring.security.resolver;

import com.keencho.lib.spring.common.exception.KcSystemException;
import com.keencho.lib.spring.security.resolver.annotation.KcsAccount;
import com.keencho.lib.spring.security.resolver.manager.KcAccountResolverManager;
import com.keencho.lib.spring.security.utils.KcSecurityUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class KcWebAccountResolver implements HandlerMethodArgumentResolver {

    private final KcAccountResolverManager kcAccountResolverManager;

    public KcWebAccountResolver(KcAccountResolverManager kcAccountResolverManager) {
        this.kcAccountResolverManager = kcAccountResolverManager;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(KcsAccount.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        if (this.supportsParameter(parameter)) {
            var kcsAccount = parameter.getParameterAnnotation(KcsAccount.class);
            var securityAccount = KcSecurityUtils.getKcSecurityAccount();

            Object account = null;
            if (securityAccount != null) {
                switch (kcsAccount.accountType()) {
                    case ACCOUNT_ENTITY -> {
                        if (securityAccount.getAccountEntityClass() == parameter.getParameterType()) {
                            var resolver = this.kcAccountResolverManager.getKcAccountResolver(securityAccount.getAccountEntityClass());

                            if (resolver != null) {
                                account = resolver.getAccountBySecurityAccount(securityAccount);
                            }
                        }
                    }
                    case SECURITY_ACCOUNT -> account = securityAccount;
                    case CUSTOM_OBJECT -> {

                    }
                }
            }

            if (kcsAccount.required() && account == null) {
                throw new KcSystemException("KcsAccount must not be null");
            }

            return account;

        }

        return WebArgumentResolver.UNRESOLVED;
    }
}
