package com.keencho.lib.spring.jpa.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface KcJpaRepository<T, ID> extends JpaRepository<T, ID>, KcQueryExecutor<T> {
}
