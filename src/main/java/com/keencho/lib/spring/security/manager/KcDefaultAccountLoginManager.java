package com.keencho.lib.spring.security.manager;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public class KcDefaultAccountLoginManager<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID> implements KcAccountLoginManager<T, R, ID> {

    private final R repo;

    public KcDefaultAccountLoginManager(R r) {
        this.repo = r;
    }

    @Override
    public T findByLoginId(String loginId) {
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

    @Override
    public int updateLoginAttemptAccount(String loginId) {
        var account = this.findByLoginId(loginId);

        if (account == null) {
            return 0;
        }

        var currentCount = account.getLoginAttemptCount();
        var targetCount = currentCount + 1;

        if (targetCount >= account.maxLoginAttemptCount()) {
            account.setAccountNonLocked(false);
        }

        account.setLoginAttemptCount(targetCount);

        repo.save(account);

        return targetCount;
    }
}
