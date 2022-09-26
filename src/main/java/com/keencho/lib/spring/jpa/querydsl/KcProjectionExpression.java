package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QBean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class KcProjectionExpression<T> {

    private final Class<? extends T> type;

    public KcProjectionExpression(Class<? extends T> type) {
        this.type = type;
    }

    public QBean<T> build() {
        var map = new LinkedHashMap<String, Expression<?>>();
        for (var declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                var v = declaredField.get(this);
                if (v != null) {
                    map.put(declaredField.getName(), (Expression<?>) v);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return new KcQBean<>(type, map);
    }
}
