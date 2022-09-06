package com.keencho.lib.spring.excel.resolver;

@FunctionalInterface
public interface KcExcelMaskingResolver {
    String apply(Object value);
}
