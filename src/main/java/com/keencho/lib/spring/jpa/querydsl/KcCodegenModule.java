package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.codegen.*;

import java.util.Collections;

import static com.querydsl.codegen.BeanSerializer.DEFAULT_JAVADOC_SUFFIX;

public class KcCodegenModule extends AbstractModule {

    /**
     * key for the query type name prefix
     */
    public static final String PREFIX = "prefix";

    /**
     * key for the query type name suffix
     */
    public static final String SUFFIX = "suffix";

    /**
     * key for the keywords set
     */
    public static final String KEYWORDS = "keywords";

    /**
     * key for the package suffix
     */
    public static final String PACKAGE_SUFFIX = "packageSuffix";

    /**
     * key for the custom imports set
     */
    public static final String IMPORTS = "imports";

    /**
     * key for the variable name function class
     */
    public static final String VARIABLE_NAME_FUNCTION_CLASS = "variableNameFunction";

    /**
     * the fully qualified class name of the <em>Single-Element Annotation</em> (with {@code String} element)
     * to indicate that these have been generated. Defaults to java's {@code Generated} annotation (depending on java version)
     */
    public static final String GENERATED_ANNOTATION_CLASS = "generatedAnnotationClass";

    protected static final String JAVADOC_SUFFIX = "javadocSuffix";

    @Override
    protected void configure() {
        bind(TypeMappings.class, JavaTypeMappings.class);
        bind(QueryTypeFactory.class, QueryTypeFactoryImpl.class);
        bind(EntitySerializer.class, DefaultEntitySerializer.class);
        bind(EmbeddableSerializer.class, DefaultEmbeddableSerializer.class);
        bind(ProjectionSerializer.class, KcProjectionSerializer.class);
        bind(SupertypeSerializer.class, DefaultSupertypeSerializer.class);
        bind(Filer.class, DefaultFiler.class);

        // configuration for QueryTypeFactory
        bind(PREFIX, "Q");
        bind(SUFFIX, "");
        bind(PACKAGE_SUFFIX, "");
        bind(KEYWORDS, Collections.<String>emptySet());
        bind(IMPORTS, Collections.<String>emptySet());
        bind(VARIABLE_NAME_FUNCTION_CLASS, DefaultVariableNameFunction.INSTANCE);
        bindInstance(GENERATED_ANNOTATION_CLASS, GeneratedAnnotationResolver.resolveDefault());
        bind(JAVADOC_SUFFIX, DEFAULT_JAVADOC_SUFFIX);
    }

}
