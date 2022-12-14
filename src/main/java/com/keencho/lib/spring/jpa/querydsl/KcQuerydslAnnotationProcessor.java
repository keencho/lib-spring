package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.apt.AbstractQuerydslProcessor;
import com.querydsl.apt.Configuration;
import com.querydsl.apt.ExtendedTypeFactory;
import com.querydsl.apt.jpa.JPAAnnotationProcessor;
import com.querydsl.apt.jpa.JPAConfiguration;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.utils.model.TypeCategory;
import jakarta.persistence.*;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

@SupportedAnnotationTypes({ "com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection" })
public class KcQuerydslAnnotationProcessor extends AbstractQuerydslProcessor {

    private RoundEnvironment roundEnv;
    private Configuration conf;
    private ExtendedTypeFactory typeFactory;

    @Override
    protected Configuration createConfiguration(RoundEnvironment roundEnv) {
        Class<? extends Annotation> entity = Entity.class;
        Class<? extends Annotation> superType = MappedSuperclass.class;
        Class<? extends Annotation> embeddable = Embeddable.class;
        Class<? extends Annotation> embedded = Embedded.class;
        Class<? extends Annotation> skip = Transient.class;
        return new JPAConfiguration(roundEnv, processingEnv,
                entity, superType, embeddable, embedded, skip);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return JPAAnnotationProcessor.ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        this.roundEnv = roundEnv;
        this.conf = this.createConfiguration(this.roundEnv);
        this.typeFactory = new ExtendedTypeFactory(processingEnv, conf.getEntityAnnotations(), conf.getTypeMappings(), conf.getQueryTypeFactory(), conf.getVariableNameFunction());
        this.generateAndSerialize();

        return JPAAnnotationProcessor.ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    private void generateAndSerialize() {
        var kcQueryProjectionElement = this.roundEnv.getElementsAnnotatedWith(KcQueryProjection.class);

        for (var element : kcQueryProjectionElement) {
            var typeElement = (TypeElement) element;
            var model = this.getEntityType(typeElement);

            var serializer = new KcProjectionSerializer(element.getAnnotation(KcQueryProjection.class).useSetter());
            var fullPackageClassName = serializer.getKcFullPackageName(model);

            try (Writer w = conf.getFiler().createFile(processingEnv, fullPackageClassName, Collections.singleton(typeElement))) {
                var writer = new KcJavaWriter(w);
                serializer.serialize(model, writer);
            } catch (IOException ignored) { }

        }
    }

    private EntityType getEntityType(TypeElement typeElement) {
        var type = this.typeFactory.getType(typeElement.asType(), true);
        var entityType = new EntityType(type.as(TypeCategory.ENTITY), this.conf.getVariableNameFunction());

        for (var field : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
            entityType.addProperty(new Property(entityType, field.getSimpleName().toString(), this.typeFactory.getType(field.asType(), true)));
        }

        return entityType;
    }

}
