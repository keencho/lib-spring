package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.codegen.utils.*;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Parameter;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.core.types.Expression;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class KcJavaWriter {

    public static final String KC_PREFIX = "Kc";

    private static final String PRIVATE = "private ";

    private static final String PUBLIC = "public ";

    private final Set<String> classes = new HashSet<>();

    private final Set<String> packages = new HashSet<>();

    private static final String BUILDER = "builder ";

    private final Appendable appendable;
    private String indent = "";
    private final int spaces;
    private final String spacesString;

    public KcJavaWriter(Appendable appendable) {
        this.appendable = appendable;
        this.spaces = 4;
        this.spacesString = StringUtils.repeat(' ', spaces);
        this.packages.add("java.lang");
    }

    private KcJavaWriter param(Parameter parameter) throws IOException {
        append(parameter.getType().getGenericName(true, packages, classes));
        append(" ");
        append(parameter.getName());
        return this;
    }

    public KcJavaWriter nl() throws IOException {
        return append(System.lineSeparator());
    }

    public KcJavaWriter line(String... segments) throws IOException {
        append(indent);
        for (String segment : segments) {
            append(segment);
        }
        return nl();
    }

    public KcJavaWriter append(CharSequence csq) throws IOException {
        appendable.append(csq);
        return this;
    }

    public KcJavaWriter beginLine(String... segments) throws IOException {
        append(indent);
        for (String segment : segments) {
            append(segment);
        }
        return this;
    }

    public KcJavaWriter goIn() {
        indent += spacesString;
        return this;
    }

    public KcJavaWriter goOut() {
        if (indent.length() >= spaces) {
            indent = indent.substring(0, indent.length() - spaces);
        }
        return this;
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
}
