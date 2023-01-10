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
import java.util.Set;

@SupportedAnnotationTypes({ "com.querydsl.core.annotations.*", "jakarta.persistence.*", "javax.persistence.*"})
public class KcQuerydslAnnotationProcessor extends JPAAnnotationProcessor {

    private RoundEnvironment roundEnv;
    private Configuration conf;
    private ExtendedTypeFactory typeFactory;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return JPAAnnotationProcessor.ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        // 아래 process는 필요가 없다.
        // gradle을 기준으로 볼때 현재 라이브러리를 implementation으로 가져오고 annotationProcessor로 process 한다고 가정하자.
        // 그렇다면 현재 프로젝트 내에 있는 java.annotation.processing.Processor 파일에서 이 클래스를 찾아 수행할 것이다.
        // 문제는 그 다음인데, 이 프로젝트 / 이 클래스를 만들기 위해선 querydsl-apt 라이브러리를 무조건 추가해 줘야한다.
        // 추가된 querydsl-apt 라이브러리의 java.annotation.processing.Processor 파일에 process가 한번 더 수행되게 된다.
        // 그때 '파일을 다시 만들 수 없다' 는 에러가 발생하면서 작업이 종료된다.
        // 실제 프로젝트에서 `JPAAnnotationProcessor` 를 process 목록에서 제거하는 방법도 있겠지만 일단 아래와 같이 처리한다.
        // JPAAnnotationProcessor는 JPAAnnotationProcessor 대로 돌고 이건 이거대로 돌자.
//        var result = super.process(annotations, roundEnv);

        this.roundEnv = roundEnv;
        this.conf = super.createConfiguration(this.roundEnv);
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
