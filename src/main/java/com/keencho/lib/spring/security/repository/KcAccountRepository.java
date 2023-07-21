package com.keencho.lib.spring.security.repository;

import com.keencho.lib.spring.jpa.querydsl.repository.KcJpaRepository;
import com.keencho.lib.spring.security.model.KcAccountBaseModel;

/**
 * 계정 기본 레포지토리
 *
 * 비즈니스 로직에 따라 메소드 추가하여 사용해도 될듯.
 * @param <T> entity extends KcAccountBase
 * @param <ID> entity id
 */
public interface KcAccountRepository<T extends KcAccountBaseModel, ID> extends KcJpaRepository<T, ID> {
    T findByLoginId(String loginId);
}
