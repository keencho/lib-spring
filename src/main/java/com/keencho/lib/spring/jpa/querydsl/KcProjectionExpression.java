package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.common.exception.KcSystemException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QBean;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;

public class KcProjectionExpression<T> {

    private final Class<? extends T> type;

    public KcProjectionExpression(Class<? extends T> type) {
        this.type = type;
    }

    public QBean<T> build() {
        var map = new LinkedHashMap<String, Expression<?>>();
        for (var declaredField : this.getClass().getDeclaredFields()) {
            var modifiers = declaredField.getModifiers();
            if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                declaredField.setAccessible(true);
                try {
                    var v = declaredField.get(this);
                    if (v != null) {
                        map.put(declaredField.getName(), (Expression<?>) v);
                    }
                } catch (IllegalAccessException e) {
                    throw new KcSystemException(e.getMessage());
                }
            }
        }

        return new KcQBean<>(type, map);
    }
}
