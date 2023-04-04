package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.apt.Configuration;
import com.querydsl.apt.ExtendedTypeFactory;
import com.querydsl.apt.jpa.JPAAnnotationProcessor;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.utils.model.TypeCategory;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

@SupportedAnnotationTypes({ "com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection" })
public class KcQuerydslAnnotationProcessor extends JPAAnnotationProcessor {

    private RoundEnvironment roundEnv;
    private Configuration conf;
    private ExtendedTypeFactory typeFactory;

    // TODO: 지금보니까 왜 JPAAnnotationProcessor을 상속하게 만들어뒀지? 그냥 바꾸자.
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

            var serializer = new KcProjectionSerializer();
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
            var modifiers = field.getModifiers();

            // exclude final, static field
            if (modifiers.stream().noneMatch(modifier -> modifier == Modifier.FINAL || modifier == Modifier.STATIC)) {
                entityType.addProperty(new Property(entityType, field.getSimpleName().toString(), this.typeFactory.getType(field.asType(), true)));
            }
        }

        return entityType;
    }

}
