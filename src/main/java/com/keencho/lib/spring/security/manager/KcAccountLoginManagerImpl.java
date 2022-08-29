package com.keencho.lib.spring.security.manager;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public class KcAccountLoginManagerImpl<ACCOUNT extends KcAccountBaseModel, REPO extends KcAccountRepository<ACCOUNT, KEY>, KEY> implements KcAccountLoginManager<ACCOUNT, REPO, KEY> {

    private final REPO repo;

    public KcAccountLoginManagerImpl(REPO repo) {
        this.repo = repo;
    }

    @Override
    public ACCOUNT findByLoginId(String loginId) {
        return repo.findByLoginId(loginId);
    }

    @Override
    public void updateDtPasswordChangedAt(String loginId) {
        var account = this.findByLoginId(loginId);

        Assert.notNull(account, "account must not be null!");

        account.setDtPasswordChangedAt(LocalDateTime.now());

        repo.save(account);
    }

    @Override
    public void updateDtLastAccessedAt(String loginId) {
        var account = this.findByLoginId(loginId);

        Assert.notNull(account, "account must not be null!");

        account.setDtLastAccessedAt(LocalDateTime.now());

        repo.save(account);
    }
}
