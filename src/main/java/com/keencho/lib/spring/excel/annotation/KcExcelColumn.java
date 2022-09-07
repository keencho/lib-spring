package com.keencho.lib.spring.excel.annotation;

import com.keencho.lib.spring.excel.resolver.KcExcelMaskingDefaultResolver;
import com.keencho.lib.spring.excel.style.KcExcelCellStyleDefaultConfigurer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KcExcelColumn {

    String headerName() default "";

    Class<? extends KcExcelMaskingDefaultResolver> resolver() default KcExcelMaskingDefaultResolver.class;

    short width() default 125;

    Class<? extends KcExcelCellStyleDefaultConfigurer> headerStyleConfigurer() default KcExcelCellStyleDefaultConfigurer.class;
    Class<? extends KcExcelCellStyleDefaultConfigurer> bodyStyleConfigurer() default KcExcelCellStyleDefaultConfigurer.class;

}
