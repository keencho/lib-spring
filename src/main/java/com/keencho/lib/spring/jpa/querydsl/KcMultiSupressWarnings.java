package com.keencho.lib.spring.jpa.querydsl;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@SuppressWarnings("AnnotationAsSuperInterface")
public class KcMultiSupressWarnings implements SuppressWarnings {

    private final String[] values;

    KcMultiSupressWarnings(String... values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public String[] value() {
        return values;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return SuppressWarnings.class;
    }
}
