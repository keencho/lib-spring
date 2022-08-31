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

    public abstract int getMaxLongTermNonUseAllowDay();

    public abstract UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    @Override
    public T findByLoginId(String loginId) {
        return repo.findByLoginId(loginId);
    }

    @Override
    public void updateOnLoginSuccess(String loginId) {
        var account = this.findByLoginId(loginId);

        Assert.notNull(account, "account must not be null!");

        var now = LocalDateTime.now();

        account.setDtLastAccessedAt(now);
        account.setDtLastLoggedInAt(now);
        account.setLoginAttemptCount(0);

        repo.save(account);
    }

    @Override
    public int updateLoginAttemptAccount(String loginId) {
        var account = this.findByLoginId(loginId);

        if (account == null) {
            return -1;
        }

        var currentCount = account.getLoginAttemptCount();
        var targetCount = currentCount + 1;

        if (targetCount >= this.getMaxLoginAttemptCount()) {
            // 잠궈야 한다면 로그인시도는 초기화한다.
            targetCount = 0;
            account.setAccountNonLocked(false);
        }

        account.setLoginAttemptCount(targetCount);

        repo.save(account);

        return targetCount;
    }

    @Override
    public void lockAccount(String loginId) {
        var account = this.findByLoginId(loginId);

        Assert.notNull(account, "account must not be null!");

        account.setAccountNonLocked(false);

        repo.save(account);
    }
}
