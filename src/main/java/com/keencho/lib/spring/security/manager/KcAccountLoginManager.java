package com.keencho.lib.spring.security.manager;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.transaction.annotation.Transactional;

public interface KcAccountLoginManager<ACCOUNT extends KcAccountBaseModel, REPO extends KcAccountRepository<ACCOUNT, KEY>, KEY> {

    @Transactional(readOnly = true)
    ACCOUNT findByLoginId(String loginId);

    @Transactional
    void updateDtPasswordChangedAt(String loginId);

    @Transactional
    void updateDtLastAccessedAt(String loginId);

}
