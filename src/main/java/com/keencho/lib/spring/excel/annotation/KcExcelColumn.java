package com.keencho.lib.spring.excel.annotation;

import com.keencho.lib.spring.excel.resolver.KcExcelMaskingDefaultResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KcExcelColumn {

    String headerName() default "";

    Class<? extends KcExcelMaskingDefaultResolver> resolver() default KcExcelMaskingDefaultResolver.class;

}
