package com.keencho.lib.spring.test.repository;

import com.keencho.lib.spring.jpa.querydsl.repository.KcJpaRepository;
import com.keencho.lib.spring.test.model.Order_2206;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Order_2206Repository extends KcJpaRepository<Order_2206, String> {
    List<Order_2206> findByToNameLike(String toName);
}
