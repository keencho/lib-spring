package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;

public interface KcLoginService<ACCOUNT extends KcAccountBaseModel, REPO extends KcAccountRepository<ACCOUNT, KEY>, KEY, LOGIN_DATA> {
    LOGIN_DATA login(String loginId, String password);
}
