package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.codegen.*;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.lang.NonNull;

import javax.annotation.processing.Generated;
import java.io.IOException;

public class KcProjectionSerializer {

    private static final String CLASS_PREFIX = "KcQ";

    public static void serialize(final EntityType model, @NonNull KcJavaWriter writer) throws IOException {

        // package
        writer.line("package ", getPackageWithoutClassName(model), ";").nl();

        // imports
        writer.line("import ", NumberExpression.class.getPackageName(), ".*;");
        writer.line("import ", KcQBean.class.getName(), ";");

        // javadoc
        writer.line("/**");
        writer.line(" * " + getKcFullPackageName(model) + " is a KcQuerydsl Projection type for " + model.getSimpleName());
        writer.line(" */");

        // generated annotation
        writer.line("@", Generated.class.getName(), "(\"", KcProjectionSerializer.class.getName(), "\")");

        // init class
        String className = getKcQClassName(model);

        writer.beginLine("public class " + className);
        writer.append(" extends ").append(KcQBean.class.getSimpleName()).append("<").append(model.getSimpleName()).append(">").append(" {");
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

    public static String getPackageWithoutClassName(EntityType entityType) {
        return entityType.getInnerType().getPackageName();
    }

    public static String getKcFullPackageName(EntityType entityType) {
        return getPackageWithoutClassName(entityType) + "." + CLASS_PREFIX + entityType.getInnerType().getSimpleName();
    }

    public static String getKcQClassName(EntityType entityType) {
        return CLASS_PREFIX + entityType.getInnerType().getSimpleName();
    }

}
