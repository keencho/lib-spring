package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QBean;

import java.util.Map;

public class KcQBean<T> extends QBean<T> {
    protected KcQBean(Class<? extends T> type, Map<String, ? extends Expression<?>> bindings) {
        super(type, true, bindings);
    }
}
