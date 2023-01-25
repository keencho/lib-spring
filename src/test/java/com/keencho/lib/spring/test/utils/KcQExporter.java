package com.keencho.lib.spring.test.utils;

import com.keencho.lib.spring.jpa.querydsl.KcJavaWriter;
import com.keencho.lib.spring.jpa.querydsl.KcProjectionSerializer;
import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.codegen.ClassPathUtils;
import com.querydsl.codegen.TypeFactory;
import com.querydsl.core.QueryException;

import java.io.*;
import java.nio.charset.Charset;

/**
 * KcQ 클래스 생성 유틸
 *
 * test scope 에만 사용할것.
 */
public class KcQExporter {

    private final ClassLoader classLoader;

    private final TypeFactory typeFactory;

    private File targetFolder;

    public KcQExporter() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.typeFactory = new TypeFactory();
    }

    public void setTargetFolder(File file) {
        this.targetFolder = file;
    }

    public void export(String packages) {
        if (packages == null) return;
        if (targetFolder == null) return;

        try {
            for (var cl : ClassPathUtils.scanPackage(classLoader, packages)) {
                if (cl.isAnnotationPresent(KcQueryProjection.class)) {

                    var serializer = new KcProjectionSerializer(cl.getAnnotation(KcQueryProjection.class).useSetter());
                    var type = typeFactory.getEntityType(cl);
                    var fullPackageClassName = serializer.getKcFullPackageName(type).replace('.', '/') + ".java";

                    var tf = new File(targetFolder, fullPackageClassName);

                    if (!tf.getParentFile().exists() && !tf.getParentFile().mkdirs()) {
                        throw new RuntimeException("failed to create target folder");
                    }

                    try(var fw = new FileWriter(tf, Charset.defaultCharset())) {
                        var writer = new KcJavaWriter(fw);
                        serializer.serialize(type, writer);
                    }

                    break;
                }
            }
        } catch (IOException e) {
            throw new QueryException(e);
        }


    }
}
