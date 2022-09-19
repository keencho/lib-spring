package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.codegen.ClassPathUtils;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.GenericExporter;
import com.querydsl.core.QueryException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class KcGenericExporter extends GenericExporter {

    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private final Map<Class<?>, EntityType> kcProjectionTypes = new HashMap<>();

    @Override
    public void export(Package... packages) {
        // 부모클래스 export 실행
        super.export(packages);

        // custom query projection code generation 실행

        String[] pkgs = new String[packages.length];
        for (int i = 0; i < packages.length; i++) {
            pkgs[i] = packages[i].getName();
        }

        this.scanPackagesAndSetProjectionTypes(pkgs);

        // process projections
//        for (Class<?> cl : projectionTypes.keySet()) {
//            createEntityType(cl, projectionTypes);
//        }

        for (var et : this.kcProjectionTypes.entrySet()) {
            this.addBuilder(et.getKey(), et.getValue());
        }
    }

    private void scanPackagesAndSetProjectionTypes(String ...packages) {
        if (packages == null) {
            return;
        }
        for (String pkg : packages) {
            try {
                for (Class<?> cl : ClassPathUtils.scanPackage(classLoader, pkg)) {
                    for (Constructor<?> constructor : cl.getConstructors()) {
                        if (constructor.isAnnotationPresent(KcQueryProjection.class)) {
                            kcProjectionTypes.put(cl, null);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new QueryException(e);
            }
        }
    }

    private void addBuilder(Class<?> cl, EntityType type) {
        System.out.println(cl.getName());
    }
}
