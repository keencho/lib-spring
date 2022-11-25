package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QBean;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class KcQBean<T> extends QBean<T> {

    private final Class<? extends T> type;
    public final boolean isBuild;
    private Map<String, Expression<?>> bindings;

    public KcQBean(Class<? extends T> type) {
        super(type);
        this.type = type;
        this.isBuild = false;
    }

    public KcQBean(Class<? extends T> type, Map<String, Expression<?>> bindings) {
        super(type, true, bindings);
        this.bindings = Collections.unmodifiableMap(new LinkedHashMap<>(bindings));
        this.type = type;
        this.isBuild = true;
    }

    public Map<String, Expression<?>> getBindings() {
        return this.bindings;
    }

    public KcQBean<T> build() {
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
                } catch (IllegalAccessException ignored) { }
            }
        }

        return new KcQBean<>(this.type, bindings);
    }
}
