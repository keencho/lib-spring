package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.codegen.utils.*;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Parameter;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeExtends;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Param;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KcJavaWriter extends AbstractCodeWriter<KcJavaWriter> {
    
    private static final String EXTENDS = " extends ";

    private static final String IMPLEMENTS = " implements ";

    private static final String IMPORT = "import ";

    private static final String IMPORT_STATIC = "import static ";

    private static final String PACKAGE = "package ";

    private static final String PRIVATE = "private ";

    private static final String PRIVATE_FINAL = "private final ";

    private static final String PRIVATE_STATIC_FINAL = "private static final ";

    private static final String PROTECTED = "protected ";

    private static final String PROTECTED_FINAL = "protected final ";

    private static final String PUBLIC = "public ";

    private static final String PUBLIC_CLASS = "public class ";

    private static final String PUBLIC_FINAL = "public final ";

    private static final String PUBLIC_INTERFACE = "public interface ";

    private static final String PUBLIC_STATIC = "public static ";

    private static final String PUBLIC_STATIC_FINAL = "public static final ";

    private final Set<String> classes = new HashSet<String>();

    private final Set<String> packages = new HashSet<String>();

    private final Stack<Type> types = new Stack<Type>();

    private static final String BUILDER = "builder ";

    public KcJavaWriter(Appendable appendable) {
        super(appendable, 4);
        this.packages.add("java.lang");
    }

    @Override
    public KcJavaWriter annotation(Annotation annotation) throws IOException {
        beginLine().append("@").appendType(annotation.annotationType());
        Method[] methods = annotation.annotationType().getDeclaredMethods();
        if (methods.length == 1 && methods[0].getName().equals("value")) {
            try {
                Object value = methods[0].invoke(annotation);
                append("(");
                annotationConstant(value);
                append(")");
            } catch (IllegalArgumentException e) {
                throw new CodegenException(e);
            } catch (IllegalAccessException e) {
                throw new CodegenException(e);
            } catch (InvocationTargetException e) {
                throw new CodegenException(e);
            }
        } else {
            boolean first = true;
            for (Method method : methods) {
                try {
                    Object value = method.invoke(annotation);
                    if (value == null || value.equals(method.getDefaultValue())) {
                        continue;
                    } else if (value.getClass().isArray()
                            && Arrays.equals((Object[]) value, (Object[]) method.getDefaultValue())) {
                        continue;
                    } else if (!first) {
                        append(Symbols.COMMA);
                    } else {
                        append("(");
                    }
                    append(method.getName()).append("=");
                    annotationConstant(value);
                } catch (IllegalArgumentException e) {
                    throw new CodegenException(e);
                } catch (IllegalAccessException e) {
                    throw new CodegenException(e);
                } catch (InvocationTargetException e) {
                    throw new CodegenException(e);
                }
                first = false;
            }
            if (!first) {
                append(")");
            }
        }
        return nl();
    }

    @Override
    public KcJavaWriter annotation(Class<? extends Annotation> annotation) throws IOException {
        return beginLine().append("@").appendType(annotation).nl();
    }

    @SuppressWarnings("unchecked")
    private void annotationConstant(Object value) throws IOException {
        if (value.getClass().isArray()) {
            append("{");
            boolean first = true;
            for (Object o : (Object[]) value) {
                if (!first) {
                    append(", ");
                }
                annotationConstant(o);
                first = false;
            }
            append("}");
        } else if (value instanceof Class) {
            appendType((Class) value).append(".class");
        } else if (value instanceof Number || value instanceof Boolean) {
            append(value.toString());
        } else if (value instanceof Enum) {
            Enum<?> enumValue = (Enum<?>) value;
            if (classes.contains(enumValue.getClass().getName())
                    || packages.contains(enumValue.getClass().getPackage().getName())) {
                append(enumValue.name());
            } else {
                append(enumValue.getDeclaringClass().getName()).append(Symbols.DOT).append(enumValue.name());
            }
        } else if (value instanceof String) {
            String escaped = StringUtils.escapeJava(value.toString());
            append(Symbols.QUOTE).append(escaped.replace("\\/", "/")).append(Symbols.QUOTE);
        } else {
            throw new IllegalArgumentException("Unsupported annotation value : " + value);
        }
    }

    private KcJavaWriter appendType(Class<?> type) throws IOException {
        if (classes.contains(type.getName()) || packages.contains(type.getPackage().getName())) {
            append(type.getSimpleName());
        } else {
            append(type.getName());
        }
        return this;
    }

    @Override
    public KcJavaWriter beginClass(Type type) throws IOException {
        return beginClass(type, null);
    }

    @Override
    public KcJavaWriter beginClass(Type type, Type superClass, Type... interfaces) throws IOException {
        packages.add(type.getPackageName());
        beginLine(PUBLIC_CLASS, type.getGenericName(false, packages, classes));
        if (superClass != null) {
            append(EXTENDS).append(superClass.getGenericName(false, packages, classes));
        }
        if (interfaces.length > 0) {
            append(IMPLEMENTS);
            for (int i = 0; i < interfaces.length; i++) {
                if (i > 0) {
                    append(Symbols.COMMA);
                }
                append(interfaces[i].getGenericName(false, packages, classes));
            }
        }
        append(" {").nl().nl();
        goIn();
        types.push(type);
        return this;
    }

    @Override
    public <T> KcJavaWriter beginConstructor(Collection<T> parameters,
                                           Function<T, Parameter> transformer) throws IOException {
        types.push(types.peek());
        beginLine(PUBLIC, types.peek().getSimpleName()).params(parameters, transformer)
                .append(" {").nl();
        return goIn();
    }

    @Override
    public KcJavaWriter beginConstructor(Parameter... parameters) throws IOException {
        types.push(types.peek());
        beginLine(PUBLIC, types.peek().getSimpleName()).params(parameters).append(" {").nl();
        return goIn();
    }

    @Override
    public KcJavaWriter beginInterface(Type type, Type... interfaces) throws IOException {
        packages.add(type.getPackageName());
        beginLine(PUBLIC_INTERFACE, type.getGenericName(false, packages, classes));
        if (interfaces.length > 0) {
            append(EXTENDS);
            for (int i = 0; i < interfaces.length; i++) {
                if (i > 0) {
                    append(Symbols.COMMA);
                }
                append(interfaces[i].getGenericName(false, packages, classes));
            }
        }
        append(" {").nl().nl();
        goIn();
        types.push(type);
        return this;
    }

    private KcJavaWriter beginMethod(String modifiers, Type returnType, String methodName,
                                   Parameter... args) throws IOException {
        types.push(types.peek());
        beginLine(
                modifiers, returnType.getGenericName(true, packages, classes), Symbols.SPACE, methodName)
                .params(args).append(" {").nl();
        return goIn();
    }

    @Override
    public <T> KcJavaWriter beginPublicMethod(Type returnType, String methodName,
                                            Collection<T> parameters, Function<T, Parameter> transformer) throws IOException {
        return beginMethod(PUBLIC, returnType, methodName, transform(parameters, transformer));
    }

    @Override
    public KcJavaWriter beginPublicMethod(Type returnType, String methodName, Parameter... args)
            throws IOException {
        return beginMethod(PUBLIC, returnType, methodName, args);
    }

    @Override
    public <T> KcJavaWriter beginStaticMethod(Type returnType, String methodName,
                                            Collection<T> parameters, Function<T, Parameter> transformer) throws IOException {
        return beginMethod(PUBLIC_STATIC, returnType, methodName,
                transform(parameters, transformer));
    }

    @Override
    public KcJavaWriter beginStaticMethod(Type returnType, String methodName, Parameter... args)
            throws IOException {
        return beginMethod(PUBLIC_STATIC, returnType, methodName, args);
    }

    @Override
    public KcJavaWriter end() throws IOException {
        types.pop();
        goOut();
        return line("}").nl();
    }

    @Override
    public KcJavaWriter field(Type type, String name) throws IOException {
        return line(type.getGenericName(true, packages, classes), Symbols.SPACE, name, Symbols.SEMICOLON).nl();
    }

    private KcJavaWriter field(String modifier, Type type, String name) throws IOException {
        return line(
                modifier, type.getGenericName(true, packages, classes), Symbols.SPACE, name, Symbols.SEMICOLON)
                .nl();
    }

    private KcJavaWriter field(String modifier, Type type, String name, String value)
            throws IOException {
        return line(
                modifier, type.getGenericName(true, packages, classes), Symbols.SPACE, name,
                Symbols.ASSIGN, value, Symbols.SEMICOLON).nl();
    }


    @Override
    public String getClassConstant(String className) {
        return className + ".class";
    }

    @Override
    public String getGenericName(boolean asArgType, Type type) {
        return type.getGenericName(asArgType, packages, classes);
    }

    @Override
    public String getRawName(Type type) {
        return type.getRawName(packages, classes);
    }

    @Override
    public KcJavaWriter imports(Class<?>... imports) throws IOException {
        for (Class<?> cl : imports) {
            classes.add(cl.getName());
            line(IMPORT, cl.getName(), Symbols.SEMICOLON);
        }
        nl();
        return this;
    }

    @Override
    public KcJavaWriter imports(Package... imports) throws IOException {
        for (Package p : imports) {
            packages.add(p.getName());
            line(IMPORT, p.getName(), ".*;");
        }
        nl();
        return this;
    }

    @Override
    public KcJavaWriter importClasses(String... imports) throws IOException {
        for (String cl : imports) {
            classes.add(cl);
            line(IMPORT, cl, Symbols.SEMICOLON);
        }
        nl();
        return this;
    }

    @Override
    public KcJavaWriter importPackages(String... imports) throws IOException {
        for (String p : imports) {
            packages.add(p);
            line(IMPORT, p, ".*;");
        }
        nl();
        return this;
    }

    @Override
    public KcJavaWriter javadoc(String... lines) throws IOException {
        line("/**");
        for (String line : lines) {
            line(" * ", line);
        }
        return line(" */");
    }

    @Override
    public KcJavaWriter packageDecl(String packageName) throws IOException {
        packages.add(packageName);
        return line(PACKAGE, packageName, Symbols.SEMICOLON).nl();
    }

    private <T> KcJavaWriter params(Collection<T> parameters, Function<T, Parameter> transformer)
            throws IOException {
        append("(");
        boolean first = true;
        for (T param : parameters) {
            if (!first) {
                append(Symbols.COMMA);
            }
            param(transformer.apply(param));
            first = false;
        }
        append(")");
        return this;
    }

    private KcJavaWriter params(Parameter... params) throws IOException {
        append("(");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                append(Symbols.COMMA);
            }
            param(params[i]);
        }
        append(")");
        return this;
    }

    private KcJavaWriter param(Parameter parameter) throws IOException {
        append(parameter.getType().getGenericName(true, packages, classes));
        append(" ");
        append(parameter.getName());
        return this;
    }

    @Override
    public KcJavaWriter privateField(Type type, String name) throws IOException {
        return field(PRIVATE, type, name);
    }

    @Override
    public KcJavaWriter privateFinal(Type type, String name) throws IOException {
        return field(PRIVATE_FINAL, type, name);
    }

    @Override
    public KcJavaWriter privateFinal(Type type, String name, String value) throws IOException {
        return field(PRIVATE_FINAL, type, name, value);
    }

    @Override
    public KcJavaWriter privateStaticFinal(Type type, String name, String value) throws IOException {
        return field(PRIVATE_STATIC_FINAL, type, name, value);
    }

    @Override
    public KcJavaWriter protectedField(Type type, String name) throws IOException {
        return field(PROTECTED, type, name);
    }

    @Override
    public KcJavaWriter protectedFinal(Type type, String name) throws IOException {
        return field(PROTECTED_FINAL, type, name);
    }

    @Override
    public KcJavaWriter protectedFinal(Type type, String name, String value) throws IOException {
        return field(PROTECTED_FINAL, type, name, value);
    }

    @Override
    public KcJavaWriter publicField(Type type, String name) throws IOException {
        return field(PUBLIC, type, name);
    }

    @Override
    public KcJavaWriter publicField(Type type, String name, String value) throws IOException {
        return field(PUBLIC, type, name, value);
    }

    @Override
    public KcJavaWriter publicFinal(Type type, String name) throws IOException {
        return field(PUBLIC_FINAL, type, name);
    }

    @Override
    public KcJavaWriter publicFinal(Type type, String name, String value) throws IOException {
        return field(PUBLIC_FINAL, type, name, value);
    }

    @Override
    public KcJavaWriter publicStaticFinal(Type type, String name, String value) throws IOException {
        return field(PUBLIC_STATIC_FINAL, type, name, value);
    }

    @Override
    public KcJavaWriter staticimports(Class<?>... imports) throws IOException {
        for (Class<?> cl : imports) {
            line(IMPORT_STATIC, cl.getName(), ".*;");
        }
        return this;
    }

    @Override
    public KcJavaWriter suppressWarnings(String type) throws IOException {
        return line("@SuppressWarnings(\"", type, "\")");
    }

    @Override
    public KcJavaWriter suppressWarnings(String... types) throws IOException {
        return annotation(new KcMultiSupressWarnings(types));
    }

    private <T> Parameter[] transform(Collection<T> parameters,
                                      Function<T, Parameter> transformer) {
        Parameter[] rv = new Parameter[parameters.size()];
        int i = 0;
        for (T value : parameters) {
            rv[i++] = transformer.apply(value);
        }
        return rv;
    }

    // custom code
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public KcJavaWriter goIn() {
        return super.goIn();
    }

    @Override
    public KcJavaWriter goOut() {
        return super.goOut();
    }

    public KcJavaWriter emptyBuilderConstructor() throws IOException {
        return beginLine(PUBLIC_STATIC)
                .append(StringUtils.capitalize(BUILDER))
                .append(BUILDER)
                .append("() {")
                .nl()
                .goIn()
                .beginLine("return new ", StringUtils.capitalize(BUILDER), "();")
                .nl()
                .goOut()
                .beginLine("}")
                .nl()
                .nl();

    }

    public KcJavaWriter beginBuilderClass() throws IOException {
        return beginLine(PUBLIC_STATIC)
                .append("class ")
                .append(StringUtils.capitalize(BUILDER))
                .append("{")
                .nl()
                .nl()
                .goIn();
    }

    public KcJavaWriter privateExpressionField(Type type, String name) throws IOException {
        return beginLine(PRIVATE)
                .param(new Parameter(name, new ClassType(Expression.class, type)))
                .append(Symbols.SEMICOLON)
                .nl().nl();
    }

    public KcJavaWriter setterMethod(Type type, String name) throws IOException {
        return beginLine(PUBLIC)
                .param(new Parameter("set" + StringUtils.capitalize(name), new ClassType(void.class)))
                .append("(")
                .param(new Parameter(name, new ClassType(Expression.class, type)))
                .append(") {")
                .nl()
                .goIn()
                .beginLine(String.format("this.%s = %s;", name, name))
                .nl()
                .goOut()
                .beginLine("}")
                .nl().nl();
    }

    public KcJavaWriter builderMethod(Type type, String name) throws IOException {
        return beginLine(PUBLIC).append(StringUtils.capitalize(BUILDER))
                .append(name).append("(").param(new Parameter(name, new ClassType(Expression.class, type))).append(") {")
                .nl()
                .goIn()
                .beginLine(String.format("this.%s = %s;", name, name))
                .nl()
                .beginLine("return this;")
                .nl()
                .goOut()
                .beginLine("}")
                .nl().nl();
    }

    public KcJavaWriter finalBuildMethod(Collection<Parameter> parameters) throws IOException {
        return beginLine(PUBLIC)
                .append(types.peek().getSimpleName())
                .append(" build() {")
                .nl()
                .goIn()
                .beginLine("return new ", types.peek().getSimpleName(), "(")
                .append(parameters.stream().map(Parameter::getName).collect(Collectors.joining(", ")))
                .append(");")
                .nl()
                .goOut()
                .beginLine("}")
                .nl().nl();
    }
}
