package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.codegen.CodegenModule;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.SerializerConfig;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.CodeWriter;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.core.types.dsl.NumberExpression;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class KcDefaultProjectionSerializer implements KcProjectionSerializer {

    private final Class<? extends Annotation> generatedAnnotationClass;
    private final TypeMappings typeMappings;

    private static final String KC_PREFIX = "Kc";
    private String indent = "";

    @Inject
    public KcDefaultProjectionSerializer(
            TypeMappings typeMappings,
            @Named(CodegenModule.GENERATED_ANNOTATION_CLASS) Class<? extends Annotation> generatedAnnotationClass) {
        this.typeMappings = typeMappings;
        this.generatedAnnotationClass = generatedAnnotationClass;
    }

    @Override
    public void serialize(final EntityType model, SerializerConfig serializerConfig,
                          CodeWriter cw) throws IOException {

        var writer = new KcJavaWriter(cw);
        Type queryType = typeMappings.getPathType(model, model, false);

        // package
        if (!queryType.getPackageName().isEmpty()) {
            writer.line("package ", queryType.getPackageName(), ";").nl();
        }

        // imports
        writer.line("import ", NumberExpression.class.getPackageName(), ".*;");
        writer.line("import ", KcProjectionExpression.class.getName(), ";");
        writer.line("import ", generatedAnnotationClass.getName(), ";").nl();

        // javadoc
        writer.line("/**");
        writer.line(" * " + queryType + " is a KcQuerydsl Projection type for " + model.getSimpleName());
        writer.line(" */");

        // generated annotation
        writer.line("@", generatedAnnotationClass.getSimpleName(), "(\"", getClass().getName(), "\")");

        // init class
        String className = KC_PREFIX + queryType.getSimpleName();

        writer.beginLine("public class " + className);
        writer.append(" extends ").append(KcProjectionExpression.class.getSimpleName()).append("<").append(model.getSimpleName()).append(">").append(" {");
        writer.nl().nl();

        // empty constructor
        writer.goIn();
        writer.line("public ", className, "() {");
        writer.goIn();
        writer.line("super(", model.getSimpleName(), ".class);");
        writer.goOut();
        writer.line("}");
        writer.nl();

        // builder constructor
        writer.line("public ", className, "(Builder builder) {");
        writer.goIn();
        writer.line("super(", model.getSimpleName(), ".class);");

        for (var property : model.getProperties()) {
            var name = property.getName();
            writer.line("this.", name, " = builder.", name, ";");
        }

        writer.goOut();
        writer.line("}");
        writer.nl();

        // serialVersionUID
        writer.line("private static final long serialVersionUID = ", model.hashCode() + "L;");
        writer.nl();

        // field / setter
        for (var property : model.getProperties()) {
            var type = property.getType();
            var name = property.getName();

            writer.privateExpressionField(type, name);
            writer.setterMethod(type, name);
        }

        // builder method
        writer.line("public static Builder builder() {");
        writer.goIn();
        writer.line("return new Builder();");
        writer.goOut();
        writer.line("}");
        writer.nl();

        // builder class
        writer.line("public static class Builder {");
        writer.nl();
        writer.goIn();

        // builder class field / build method
        for (var property : model.getProperties()) {
            var type = property.getType();
            var name = property.getName();

            writer.privateExpressionField(type, name);
            writer.builderMethod(type, name);
        }

        // builder class final build method
        writer.line("public ", className, " build() {");
        writer.goIn();
        writer.line("return new ", className, "(this);");
        writer.goOut();
        writer.line("}");
        writer.nl();

        // close builder class
        writer.goOut();
        writer.line("}");
        writer.nl();

        // close class
        writer.goOut();
        writer.line("}");
    }

}
