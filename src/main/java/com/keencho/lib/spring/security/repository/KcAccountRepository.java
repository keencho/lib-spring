package com.keencho.lib.spring.security.repository;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KcAccountRepository<ACCOUNT extends KcAccountBaseModel, KEY> extends JpaRepository<ACCOUNT, KEY> {
    ACCOUNT findByLoginId(String loginId);
}
