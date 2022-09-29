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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"com.querydsl.core.annotations.*", "jakarta.persistence.*", "javax.persistence.*"})
public class KcQuerydslAnnotationProcessor extends JPAAnnotationProcessor {

    private RoundEnvironment roundEnv;
    private Configuration conf;
    private ExtendedTypeFactory typeFactory;

    private final Map<String, EntityType> kcProjectionTypes = new HashMap<>();
    private final Map<String, Set<TypeElement>> typeElements = new HashMap<>();


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var result = super.process(annotations, roundEnv);

        this.roundEnv = roundEnv;
        this.conf = super.createConfiguration(this.roundEnv);
        this.typeFactory = new ExtendedTypeFactory(processingEnv, conf.getEntityAnnotations(), conf.getTypeMappings(), conf.getQueryTypeFactory(), conf.getVariableNameFunction());
        this.generateTarget();
        this.serialize();

        return result;
    }

    private void generateTarget() {
        var kcQueryProjectionElement = this.roundEnv.getElementsAnnotatedWith(KcQueryProjection.class);

        for (var element : kcQueryProjectionElement) {
            var typeElement = (TypeElement) element;
            var model = this.getEntityType(typeElement);
            this.typeElements.put(model.getFullName(), Collections.singleton(typeElement));
            kcProjectionTypes.put(model.getFullName(), model);
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

    private void serialize()  {
        if (!this.kcProjectionTypes.isEmpty()) {
            for (var entityType : this.kcProjectionTypes.entrySet()) {
                var val = entityType.getValue();
                var fullPackageClassName = KcProjectionSerializer.getKcFullPackageName(val);

                try (Writer w = conf.getFiler().createFile(processingEnv, fullPackageClassName, this.typeElements.get(val.getFullName()))) {
                    var writer = new KcJavaWriter(w);
                    KcProjectionSerializer.serialize(val, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
