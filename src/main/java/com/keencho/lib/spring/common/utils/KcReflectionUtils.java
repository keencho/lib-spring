package com.keencho.lib.spring.common.utils;

import com.keencho.lib.spring.common.exception.KcSystemException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class KcReflectionUtils {

    public static Object invokeGetter(Class<?> clazz, String fieldName, Object targetData)  {
        try {
            return new PropertyDescriptor(fieldName, clazz).getReadMethod().invoke(targetData);
        } catch (IntrospectionException  | InvocationTargetException | IllegalAccessException ex) {
            throw new KcSystemException();
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ex) {
            throw new KcSystemException();
        }
    }

    public static Object invokeMethod(Method method, Class<?> clazz, Object... args) {
        try {
            return method.invoke(initNewInstance(clazz), args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new KcSystemException();
        }
    }

    public static <T> T initNewInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new KcSystemException();
        }
    }
}
