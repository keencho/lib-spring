package com.keencho.lib.spring.jpa.querydsl;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.querydsl.core.types.Expression;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({ "com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection" })
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class KcQuerydslAnnotationProcessor extends AbstractProcessor {

    private final String PREFIX = "KcQ";

    private List<? extends Element> elementList;

    // TODO: 지금보니까 왜 JPAAnnotationProcessor을 상속하게 만들어뒀지? 그냥 바꾸자.
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        var elements = roundEnv.getElementsAnnotatedWith(KcQueryProjection.class);

        if (elements == null || elements.isEmpty()) {
            return true;
        }

        for (var element : elements) {
            var typeElement = (TypeElement) element;
            var isRecord = typeElement.getKind() == ElementKind.RECORD;

            elementList = typeElement
                    .getEnclosedElements()
                    .stream()
                    .filter(el -> {

                        var excludeList = new ArrayList<Modifier>();
                        // static 필드 불허
                        excludeList.add(Modifier.STATIC);

                        // record인 경우는 final 필드 허용
                        // 일반 class의 경우 final 필드 불허
                        if (!isRecord) {
                            excludeList.add(Modifier.FINAL);
                        }

                        if (el.getModifiers().stream().anyMatch(m1 -> excludeList.stream().anyMatch(m2 -> m1 == m2))) {
                            return false;
                        }
                        return el.getKind() == ElementKind.FIELD && el instanceof VariableElement;
                    })
                    .collect(Collectors.toList());

            if (elementList.isEmpty()) continue;

            // 패키지명
            var packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();

            // 오리지널 클래스
            var originalClass = typeElement.getSimpleName();

            // 만들 클래스명
            var clazz = PREFIX + typeElement.getSimpleName();

            // 만들 클래스 with 패키지명
            var fullName = packageName + "." + clazz;

            try {
                var file = processingEnv.getFiler().createSourceFile(fullName);
                var writer = new PrintWriter(file.openWriter());

                writer.println("""
                        package %s;
                        
                        import %s;
                        import %s;
                        
                        import %s;
                        import %s;
                        
                        /**
                         * %s is a KcQuerydsl Projection type for %s
                         */
                        public class %s extends %s<%s> {
                        
                            private static final long serialVersionUID = %dL;
                        
                            public %s(Builder builder) {
                                super(%s.class, builder.buildBindings());
                            }
                            
                            public static Builder builder() {
                                return new Builder();
                            }
                            
                            public static class Builder {
                            
                                %s
                                
                                public %s build() {
                                    return new %s(this);
                                }
                                
                                public Map<String, Expression<?>> buildBindings() {
                                    Map<String, Expression<?>> bindings = new LinkedHashMap<>();
                                    %s
                                    
                                    return bindings;
                                }
                               
                            }
                            
                        }
                        """.formatted(
                        // 패지지명
                        packageName,

                        // import 1
                        isRecord ? KcRecordExpression.class.getName() : KcExpression.class.getName(),
                        Expression.class.getName(),

                        // import 2
                        LinkedHashMap.class.getName(),
                        Map.class.getName(),

                        // 주석
                        fullName, originalClass,

                        // 클래스 정의
                        clazz, isRecord ? KcRecordExpression.class.getSimpleName() : KcExpression.class.getSimpleName(), originalClass,

                        // serialVersionUID
                        fullName.hashCode(),

                        // 빌더 클래스
                        clazz,
                        originalClass,

                        // 빌더 클래스 필드
                        builderFields(),

                        // 빌더 클래스 빌드 메소드
                        clazz,
                        clazz,

                        // 빌더 클래스 바인딩
                        builderBindings()
                        )
                );

                writer.close();
            } catch (IOException ex) {

            }
        }

        return true;
    }

    private static final Map<TypeKind, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();
    static {
        PRIMITIVE_TO_WRAPPER.put(TypeKind.BOOLEAN, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.BYTE, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.CHAR, Character.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.SHORT, Short.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.INT, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.LONG, Long.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.FLOAT, Float.class);
        PRIMITIVE_TO_WRAPPER.put(TypeKind.DOUBLE, Double.class);
    }

    private String builderFields() {
        return elementList
                .stream()
                .map(element -> {
                    var variableElement = (VariableElement) element;
                    var name = variableElement.getSimpleName().toString();
                    var typeMirror = variableElement.asType();
                    var typeKind = typeMirror.getKind();
                    var typeString = typeMirror.getKind().isPrimitive() ? PRIMITIVE_TO_WRAPPER.get(typeKind).getName() : typeMirror.toString();

                    return """
                            private com.querydsl.core.types.Expression<%s> %s;
                            public Builder %s(com.querydsl.core.types.Expression<%s> %s) {
                                this.%s = %s;
                                return this;
                            }
                            """.formatted(
                            typeString, name,

                            name, typeString, name,
                            name, name
                    );
                })
                .collect(Collectors.joining("\n"))
                .trim()
                .replace("\n", "\n\t\t");
    }

    private String builderBindings() {
        return elementList
                .stream()
                .map(element -> {
                    var variableElement = (VariableElement) element;
                    var name = variableElement.getSimpleName().toString();

                    return "bindings.put(\"%s\", this.%s);".formatted(name, name);
                })
                .collect(Collectors.joining("\n"))
                .trim()
                .replace("\n", "\n\t\t\t");
    }
}
