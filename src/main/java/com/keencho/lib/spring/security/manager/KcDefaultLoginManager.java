package com.keencho.lib.spring.security.manager;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Collection;

public abstract class KcDefaultLoginManager<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID> implements KcLoginManager<T, R, ID> {

    private final R repo;

    public KcDefaultLoginManager(R r) {
        this.repo = r;
    }

    public abstract Collection<? extends GrantedAuthority> getAuthorities();

    public abstract int getMaxLoginAttemptCount();

    public abstract UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

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

        if (targetCount >= this.getMaxLoginAttemptCount()) {
            account.setAccountNonLocked(false);
        }

        account.setLoginAttemptCount(targetCount);

        repo.save(account);

        return targetCount;
    }
}
