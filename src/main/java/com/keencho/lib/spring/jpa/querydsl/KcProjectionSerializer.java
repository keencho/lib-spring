package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.codegen.*;
import com.querydsl.codegen.utils.CodeWriter;
import com.querydsl.codegen.utils.JavaWriter;
import com.querydsl.codegen.utils.StringUtils;
import com.querydsl.codegen.utils.model.*;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.lang.Nullable;
import org.springframework.util.SerializationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@code ProjectionSerializer} is a {@link Serializer} implementation for projection types
 *
 * @author tiwe
 *
 */

public class KcProjectionSerializer implements ProjectionSerializer {

    private final Class<? extends Annotation> generatedAnnotationClass;
    private final TypeMappings typeMappings;

    private static final String builderClassName = "Builder";
    private static final String expressionPathName = "com.querydsl.core.types.Expression";

    /**
     * Create a new {@code ProjectionSerializer} instance
     *
     * @param typeMappings type mappings to be used
     */
    public KcProjectionSerializer(TypeMappings typeMappings) {
        this(typeMappings, GeneratedAnnotationResolver.resolveDefault());
    }

    /**
     * Create a new {@code ProjectionSerializer} instance
     *
     * @param typeMappings type mappings to be used
     * @param generatedAnnotationClass the fully qualified class name of the <em>Single-Element Annotation</em> (with {@code String} element) to be used on the generated classes.
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7.3">Single-Element Annotation</a>
     */
    @Inject
    public KcProjectionSerializer(
            TypeMappings typeMappings,
            @Named(CodegenModule.GENERATED_ANNOTATION_CLASS) Class<? extends Annotation> generatedAnnotationClass) {
        this.typeMappings = typeMappings;
        this.generatedAnnotationClass = generatedAnnotationClass;
    }

    protected void intro(EntityType model, KcJavaWriter writer) throws IOException {
        String simpleName = model.getSimpleName();
        Type queryType = typeMappings.getPathType(model, model, false);

        // package
        if (!queryType.getPackageName().isEmpty()) {
            writer.packageDecl(queryType.getPackageName());
        }

        // imports
        writer.imports(NumberExpression.class.getPackage());
        writer.imports(ConstructorExpression.class, generatedAnnotationClass);

        Set<Integer> sizes = new HashSet<>();
        for (Constructor c : model.getConstructors()) {
            sizes.add(c.getParameters().size());
        }
        if (sizes.size() != model.getConstructors().size()) {
            writer.imports(Expression.class);
        }

        // javadoc
        writer.javadoc(queryType + " is a Querydsl Projection type for " + simpleName);

        writer.line("@", generatedAnnotationClass.getSimpleName(), "(\"", getClass().getName(), "\")");

        // class header
//        writer.suppressWarnings("serial");
        Type superType = new ClassType(TypeCategory.SIMPLE, ConstructorExpression.class, model);
        writer.beginClass(queryType, superType);
        writer.privateStaticFinal(Types.LONG_P, "serialVersionUID", model.hashCode() + "L");
    }

    protected void outro(EntityType model, KcJavaWriter writer) throws IOException {
        writer.end();
    }

    protected void builder(final EntityType model, KcJavaWriter writer) throws IOException {

        // 필드의 갯수와 생성자 argument의 갯수가 일치하는 생성자를 찾는다. 순서가 일치해야 builder 클래스 build시 올바른 Q타입 생성자를 찾을 수 있다.
        var matchConstructor = model
                .getConstructors()
                .stream()
                .filter(c -> c.getParameters().size() == model.getProperties().size())
                .findFirst()
                .orElse(null);

        if (matchConstructor != null) {
            // static builder method
            writer.emptyBuilderConstructor();

            // static builder class
            writer.beginBuilderClass();

            for (var property : matchConstructor.getParameters()) {
                var type = property.getType();
                var name = property.getName();

                writer.privateExpressionField(type, name);
                writer.setterMethod(type, name);
                writer.builderMethod(type, name);
            }

            writer.finalBuildMethod(matchConstructor.getParameters());

            writer.goOut();

            writer.beginLine("}").nl().nl();
        }
    }

    @Override
    public void serialize(final EntityType model, SerializerConfig serializerConfig,
                          CodeWriter writer) throws IOException {

        var newWriter = new KcJavaWriter(writer);

        // intro
        intro(model,newWriter);

        String localName = newWriter.getRawName(model);
        Set<Integer> sizes = new HashSet<>();

        // init constructor
        for (Constructor c : model.getConstructors()) {
            final boolean asExpr = sizes.add(c.getParameters().size());
            // begin
            newWriter.beginConstructor(c.getParameters(), new Function<Parameter,Parameter>() {
                @Override
                public Parameter apply(Parameter p) {
                    Type type;
                    if (!asExpr) {
                        type = typeMappings.getExprType(p.getType(),
                                model, false, false, true);
                    } else if (p.getType().isFinal()) {
                        type = new ClassType(Expression.class, p.getType());
                    } else {
                        type = new ClassType(Expression.class, new TypeExtends(p.getType()));
                    }
                    return new Parameter(p.getName(), type);
                }
            });

            // body
            newWriter.beginLine("super(" + newWriter.getClassConstant(localName));
            newWriter.append(", new Class<?>[]{");
            boolean first = true;

            for (Parameter p : c.getParameters()) {
                if (!first) {
                    newWriter.append(", ");
                }
                if (Types.PRIMITIVES.containsKey(p.getType())) {
                    Type primitive = Types.PRIMITIVES.get(p.getType());
                    newWriter.append(newWriter.getClassConstant(primitive.getFullName()));
                } else {
                    newWriter.append(newWriter.getClassConstant(newWriter.getRawName(p.getType())));
                }
                first = false;
            }
            newWriter.append("}");

            for (Parameter p : c.getParameters()) {
                newWriter.append(", ").append(p.getName());
            }

            // end
            newWriter.append(");\n");
            newWriter.end();
        }

        ///////////////// KcQueryProjection Custom

        // init field, setter
        var matchConstructor = model
                .getConstructors()
                .stream()
                .filter(c -> c.getParameters().size() == model.getProperties().size())
                .findFirst()
                .orElse(null);

        if (matchConstructor != null) {
            for (var property : matchConstructor.getParameters()) {
                var type = property.getType();
                var name = property.getName();

                newWriter.privateExpressionField(type, name);
                newWriter.setterMethod(type, name);
            }
        }

        // init static class builder, setter
        if (model.getJavaClass().isAnnotationPresent(KcQueryProjection.class)) {
            builder(model, newWriter);
        }

        ///////////////////////////////////////////////////

        // outro
        outro(model, newWriter);
    }



}
