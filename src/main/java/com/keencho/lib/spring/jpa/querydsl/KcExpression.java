package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KcExpression<T> extends FactoryExpressionBase<T> {

    private final Class<? extends T> type;
    private final Map<String, Expression<?>> bindings;

    public KcExpression(Class<? extends T> type, Map<String, Expression<?>> bindings) {
        super(type);
        this.type = type;

        if (bindings == null || bindings.isEmpty()) {
            throw new KcRuntimeException("bindings must not be null or empty!");
        }

        this.bindings = Collections.unmodifiableMap(
                bindings
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new))
        );
    }

    public Map<String, Expression<?>> getBindings() {
        return this.bindings;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public T newInstance(Object... a) {
        try {
            var arr = this.bindings.keySet().toArray();
            if (Map.class.isAssignableFrom(this.type)) {
                var map = new LinkedHashMap<String, Object>();

                for (var i = 0; i < a.length; i ++) {
                    var value = a[i];
                    if (value != null) {
                        map.put((String) arr[i], value);
                    }
                }

                return (T) map;
            }

            T rv = type.newInstance();
            for (var i = 0; i < a.length; i ++) {
                var value = a[i];
                if (value != null) {
                    var field = this.type.getDeclaredField((String) arr[i]);
                    field.setAccessible(true);
                    field.set(rv, value);
                }
            }

            return rv;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
    }

    @Override
    public List<Expression<?>> getArgs() {
        return new ArrayList<>(this.bindings.values());
    }
}
