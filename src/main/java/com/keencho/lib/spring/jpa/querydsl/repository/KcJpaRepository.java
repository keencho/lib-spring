package com.keencho.lib.spring.jpa.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KcJpaRepository<T, ID> extends JpaRepository<T, ID>, KcSearchQuery<T> {
}
