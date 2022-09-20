//package com.keencho.lib.spring.jpa.querydsl;
//
//import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
//import com.querydsl.codegen.ClassPathUtils;
//import com.querydsl.codegen.EntityType;
//import com.querydsl.codegen.GenericExporter;
//import com.querydsl.codegen.TypeFactory;
//import com.querydsl.codegen.utils.JavaWriter;
//import com.querydsl.codegen.utils.model.*;
//import com.querydsl.core.QueryException;
//import com.querydsl.core.types.ConstructorExpression;
//import com.querydsl.core.types.Expression;
//import com.querydsl.core.types.dsl.StringExpression;
//import org.springframework.lang.NonNull;
//
//import javax.annotation.processing.Generated;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//
//public class KcGenericExporterV0 extends GenericExporter {
//
//    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//
////    private final Map<Class<?>, EntityType> kcProjectionTypes = new HashMap<>();
//
//    private final List<Class<?>> kcQueryProjectionList = new ArrayList<>();
//
//    private File targetFolder;
//
//    @Override
//    public void setTargetFolder(File targetFolder) {
//        super.setTargetFolder(targetFolder);
//        this.targetFolder = targetFolder;
//    }
//
//    @Override
//    public void export(Package... packages) {
//        // 부모클래스 export 실행
//        super.export(packages);
//
//        // @KcQueryProjection 붙은 클래스들 export 실행
//        String[] pkgs = new String[packages.length];
//        for (int i = 0; i < packages.length; i++) {
//            pkgs[i] = packages[i].getName();
//        }
//
//        this.scanPackages(pkgs);
//        this.generate();
//    }
//
//    private void scanPackages(@NonNull String ...packages) {
//        for (String pkg : packages) {
//            try {
//                for (Class<?> cl : ClassPathUtils.scanPackage(classLoader, pkg)) {
//                    // check class
//                    if (cl.isAnnotationPresent(KcQueryProjection.class)) {
//                        kcQueryProjectionList.add(cl);
//                    }
//
//                    // check constructor - 추후
////                    for (Constructor<?> constructor : cl.getConstructors()) {
////                        if (constructor.isAnnotationPresent(KcQueryProjection.class)) {
////                            kcQueryProjectionList.add(cl);
////                            break;
////                        }
////                    }
//                }
//
//            } catch (IOException e) {
//                throw new QueryException(e);
//            }
//        }
//    }
//
//    private void generate() {
//        for (var clazz : this.kcQueryProjectionList) {
//            if (clazz.getName().equals("com.keencho.spring.jpa.querydsl.dto.SimpleDTO")) {
//                var className = clazz.getSimpleName();
//                var typeFactory = new TypeFactory();
//                var typeClass = (EntityType) typeFactory.get(clazz);
//                var superClass = new ClassType(TypeCategory.SIMPLE, ConstructorExpression.class, typeClass);
//                var prefix = "KcQ";
//                var name = prefix + className;
//
//                try {
//                    var targetFile = new File(targetFolder, String.format("%s/%s.java", clazz.getPackageName().replace('.', '/'), name));
//                    var writer = new JavaWriter(new OutputStreamWriter(new FileOutputStream(targetFile), Charset.defaultCharset()));
//
//                    // package
//                    writer.packageDecl(clazz.getPackageName());
//
//                    // imports
//                    writer.imports(StringExpression.class.getPackage());
//                    writer.imports(ConstructorExpression.class, Generated.class);
//
//                    // javadoc
//                    writer.javadoc("This is a class created by the KcQueryProjection annotation.");
//
//                    // @Generated annotation
//                    writer.line("@", Generated.class.getSimpleName(), "(\"", getClass().getName(), "\")");
//
//                    // init class
//                    writer.beginClass(typeClass, superClass);
//
//                    // declare serialVersionUID
//                    writer.privateStaticFinal(Types.LONG_P, "serialVersionUID", typeClass.hashCode() + "L");
//
//                    for (Constructor c : typeClass.getConstructors()) {
//                        final boolean asExpr = sizes.add(c.getParameters().size());
//                        // begin
//                        writer.beginConstructor(c.getParameters(), new Function<Parameter,Parameter>() {
//                            @Override
//                            public Parameter apply(Parameter p) {
//                                Type type;
//                                if (!asExpr) {
//                                    type = typeMappings.getExprType(p.getType(),
//                                            model, false, false, true);
//                                } else if (p.getType().isFinal()) {
//                                    type = new ClassType(Expression.class, p.getType());
//                                } else {
//                                    type = new ClassType(Expression.class, new TypeExtends(p.getType()));
//                                }
//                                return new Parameter(p.getName(), type);
//                            }
//                        });
//
//                        // body
//                        writer.beginLine("super(" + writer.getClassConstant(localName));
//                        // TODO: Fix for Scala (Array[Class])
//                        writer.append(", new Class<?>[]{");
//                        boolean first = true;
//
//                        for (Parameter p : c.getParameters()) {
//                            if (!first) {
//                                writer.append(", ");
//                            }
//                            if (Types.PRIMITIVES.containsKey(p.getType())) {
//                                Type primitive = Types.PRIMITIVES.get(p.getType());
//                                writer.append(writer.getClassConstant(primitive.getFullName()));
//                            } else {
//                                writer.append(writer.getClassConstant(writer.getRawName(p.getType())));
//                            }
//                            first = false;
//                        }
//                        writer.append("}");
//
//                        for (Parameter p : c.getParameters()) {
//                            writer.append(", ").append(p.getName());
//                        }
//
//                        // end
//                        writer.append(");\n");
//                        writer.end();
//                    }
//
//                    writer.end();
//
//                } catch (IOException ex) {
//                    throw new QueryException(ex);
//                }
//            }
//        }
//    }
//}
