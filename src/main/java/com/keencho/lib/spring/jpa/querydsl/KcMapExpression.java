package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.core.types.Expression;

import java.util.LinkedHashMap;
import java.util.Map;

public class KcMapExpression extends KcExpression<Map<String, Object>> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public KcMapExpression(Map<String, Expression<?>> bindings) {
        super((Class) Map.class, bindings);
    }

    @Override
    public Map<String, Object> newInstance(Object... a) {
        var arr = getBindings().keySet().toArray();
        var map = new LinkedHashMap<String, Object>();

        for (var i = 0; i < a.length; i ++) {
            var value = a[i];
            if (value != null) {
                map.put((String) arr[i], value);
            }
        }

        return map;
    }
}
