package net.smackem.nutfx.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

class NutProc {
    private final String name;
    private final List<NutProcParameter<?>> parameters;
    private final Method method;

    private NutProc(String name, List<NutProcParameter<?>> parameters, Method method) {
        this.name = name;
        this.parameters = Collections.unmodifiableList(parameters);
        this.method = method;
    }

    public static NutProc fromMethod(Method method) {
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("controller method '" + method.getName() + "' does not return void");
        }
        return new NutProc(method.getName(), convertParameters(method.getParameters()), method);
    }

    public String name() {
        return this.name;
    }

    public List<NutProcParameter<?>> parameters() {
        return this.parameters;
    }

    public Method method() {
        return this.method;
    }

    private static List<NutProcParameter<?>> convertParameters(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(NutProc::convertParameter)
                .collect(Collectors.toList());
    }

    private static NutProcParameter<?> convertParameter(Parameter parameter) {
        final Class<?> type = parameter.getType();
        if (type == int.class) {
            return NutProcParameter.integer(parameter.getName(), false);
        }
        if (type == boolean.class) {
            return NutProcParameter.bool(parameter.getName(), false);
        }
        if (type == double.class) {
            return NutProcParameter.floatingPoint(parameter.getName(), false);
        }
        if (type == String.class) {
            return NutProcParameter.string(parameter.getName(), isRequired(parameter) == false);
        }
        if (type == Integer.class) {
            return NutProcParameter.integer(parameter.getName(), isRequired(parameter) == false);
        }
        if (type == Boolean.class) {
            return NutProcParameter.bool(parameter.getName(), isRequired(parameter) == false);
        }
        if (type == Double.class) {
            return NutProcParameter.floatingPoint(parameter.getName(), isRequired(parameter) == false);
        }
        throw new IllegalArgumentException("parameter '" + parameter.getName() + "' is of unsupported type");
    }

    private static boolean isRequired(Parameter parameter) {
        return parameter.getDeclaredAnnotation(NutRequired.class) != null;
    }
}
