package com.keencho.lib.spring.excel.resolver;

public class KcExcelMaskingDefaultResolver implements KcExcelMaskingResolver {
    @Override
    public String apply(Object value) {
        return String.valueOf(value);
    }
}
