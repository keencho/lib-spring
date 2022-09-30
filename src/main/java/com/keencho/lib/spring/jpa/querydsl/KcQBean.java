package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.common.exception.KcSystemException;
import com.querydsl.core.types.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class KcQBean<T> extends QBean<T> {

    private final Class<? extends T> type;

    public KcQBean(Class<? extends T> type) {
        super(type);
        this.type = type;
    }

    public KcQBean(Class<? extends T> type, Map<String, Expression<?>> bindings) {
        super(type, true, bindings);
        this.type = type;
    }

    public QBean<T> build() {
        Map<String, Expression<?>> bindings = new HashMap<>();

        for (var declaredField : this.getClass().getDeclaredFields()) {
            var modifiers = declaredField.getModifiers();
            if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                declaredField.setAccessible(true);
                try {
                    var v = declaredField.get(this);
                    if (v != null) {
                        bindings.put(declaredField.getName(), (Expression<?>) v);
                    }
                } catch (IllegalAccessException e) {
                    throw new KcSystemException(e.getMessage());
                }
            }
        }

        return new KcQBean<>(this.type, bindings);
    }
}
