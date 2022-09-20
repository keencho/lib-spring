package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.codegen.*;
import com.querydsl.codegen.utils.CodeWriter;
import com.querydsl.codegen.utils.model.*;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * {@code ProjectionSerializer} is a {@link Serializer} implementation for projection types
 *
 * @author tiwe
 *
 */

public class KcProjectionSerializer implements ProjectionSerializer {

    private final Class<? extends Annotation> generatedAnnotationClass;
    private final TypeMappings typeMappings;

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

    protected void intro(EntityType model, CodeWriter writer) throws IOException {
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

    protected void outro(EntityType model, CodeWriter writer) throws IOException {
        writer.end();
    }

    @Override
    public void serialize(final EntityType model, SerializerConfig serializerConfig,
                          CodeWriter writer) throws IOException {
        // intro
        intro(model, writer);

        String localName = writer.getRawName(model);
        Set<Integer> sizes = new HashSet<>();

        // init constructor
        for (Constructor c : model.getConstructors()) {
            final boolean asExpr = sizes.add(c.getParameters().size());
            // begin
            writer.beginConstructor(c.getParameters(), new Function<Parameter,Parameter>() {
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
            writer.beginLine("super(" + writer.getClassConstant(localName));
            writer.append(", new Class<?>[]{");
            boolean first = true;

            for (Parameter p : c.getParameters()) {
                if (!first) {
                    writer.append(", ");
                }
                if (Types.PRIMITIVES.containsKey(p.getType())) {
                    Type primitive = Types.PRIMITIVES.get(p.getType());
                    writer.append(writer.getClassConstant(primitive.getFullName()));
                } else {
                    writer.append(writer.getClassConstant(writer.getRawName(p.getType())));
                }
                first = false;
            }
            writer.append("}");

            for (Parameter p : c.getParameters()) {
                writer.append(", ").append(p.getName());
            }

            // end
            writer.append(");\n");
            writer.end();
        }

        // init static class builder, setter
        if (model.getJavaClass().isAnnotationPresent(KcQueryProjection.class)) {
            System.out.println("여까지왔군.");
        }

        // outro
        outro(model, writer);
    }

}
