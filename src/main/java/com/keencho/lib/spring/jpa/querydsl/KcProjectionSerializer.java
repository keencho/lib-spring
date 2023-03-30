package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.codegen.EntityType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.lang.NonNull;

import javax.annotation.processing.Generated;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KcProjectionSerializer {

    private static final String CLASS_PREFIX = "KcQ";

    public KcProjectionSerializer() {
    }

    public void serialize(final EntityType model, @NonNull KcJavaWriter writer) throws IOException {
        var properties = model.getProperties();

        // package
        writer.line("package ", getPackageWithoutClassName(model), ";").nl();

        // imports
        writer.line("import ", NumberExpression.class.getPackageName(), ".*;");
        writer.line("import ", Expression.class.getName(), ";");
        writer.line("import ", KcExpression.class.getName(), ";");
        writer.line("import ", HashMap.class.getName(), ";");
        writer.line("import ", Map.class.getName(), ";");

        // javadoc
        writer.line("/**");
        writer.line(" * " + getKcFullPackageName(model) + " is a KcQuerydsl Projection type for " + model.getSimpleName());
        writer.line(" */");

        // generated annotation
        writer.line("@", Generated.class.getName(), "(\"", KcProjectionSerializer.class.getName(), "\")");

        // init class
        String className = getKcQClassName(model);

        writer.beginLine("public class " + className);
        writer.append(" extends ").append(KcExpression.class.getSimpleName()).append("<").append(model.getSimpleName()).append(">").append(" {");
        writer.nl().nl();
        writer.goIn();

        // builder constructor
        writer.line("public ", className, "(Builder builder) {");
        writer.goIn();
        writer.line("super(", model.getSimpleName(), ".class, builder.buildBindings());");

        writer.goOut();
        writer.line("}");
        writer.nl();

        // serialVersionUID
        writer.line("private static final long serialVersionUID = ", model.hashCode() + "L;");
        writer.nl();

//        // field / setter
//        for (var property : properties) {
//            var type = property.getType();
//            var name = property.getName();
//
//            writer.privateExpressionField(type, name);
//        }

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
        for (var property : properties) {
            var type = property.getType();
            var name = property.getName();

            writer.privateExpressionField(type, name);
            writer.builderMethod(type, name);
        }

        // builder class build method
        writer.line("public ", className, " build() {");
        writer.goIn();
        writer.line("return new ", className, "(this);");
        writer.goOut();
        writer.line("}");
        writer.nl();

        // builder class buildBindings method
        writer.line("public Map<String, Expression<?>> buildBindings() {");
        writer.goIn();
        writer.line("Map<String, Expression<?>> bindings = new HashMap<>();");
        for (var property : properties) {
            writer.line(String.format("bindings.put(\"%1$s\", this.%1$s);", property.getName()));
        }
        writer.line("return bindings;");
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

    public String getPackageWithoutClassName(EntityType entityType) {
        return entityType.getInnerType().getPackageName();
    }

    public String getKcFullPackageName(EntityType entityType) {
        return getPackageWithoutClassName(entityType) + "." + CLASS_PREFIX + entityType.getInnerType().getSimpleName();
    }

    public String getKcQClassName(EntityType entityType) {
        return CLASS_PREFIX + entityType.getInnerType().getSimpleName();
    }

}
