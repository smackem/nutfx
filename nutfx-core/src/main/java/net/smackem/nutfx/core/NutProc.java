package net.smackem.nutfx.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
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
        final var nutMethod = method.getDeclaredAnnotation(NutMethod.class);
        if (nutMethod == null) {
            throw new IllegalArgumentException("controller method '" + method.getName() + "' is not annotated with @NutMethod");
        }
        final var name = nutMethod.value().isBlank()
                ? method.getName()
                : nutMethod.value();
        final var parameters = convertParameters(method.getParameters());
        assertParametersUnique(parameters, name);
        return new NutProc(name, parameters, method);
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

    private static void assertParametersUnique(Collection<NutProcParameter<?>> parameters, String methodName) {
        final Set<String> parameterNames = new HashSet<>();
        for (final var parameter : parameters) {
            if (parameterNames.add(parameter.name()) == false) {
                throw new IllegalArgumentException("method '" + methodName + "': + parameter name '" + parameter.name() + "' is not unique");
            }
        }
    }

    private static NutProcParameter<?> convertParameter(Parameter parameter) {
        final var nutParam = parameter.getDeclaredAnnotation(NutParam.class);
        if (nutParam == null) {
            throw new IllegalArgumentException("parameter '" + parameter.getName() + "' is not annotated with NutParam");
        }
        final Class<?> type = parameter.getType();
        final boolean optional = type.isPrimitive() == false && nutParam.isRequired() == false;
        if (nutParam.converterClass() != void.class) {
            return NutProcParameter.custom(nutParam.value(), getConverter(nutParam.converterClass(), type), optional);
        }
        if (type == int.class || type == Integer.class) {
            return NutProcParameter.integer(nutParam.value(), optional);
        }
        if (type == boolean.class || type == Boolean.class) {
            return NutProcParameter.bool(nutParam.value(), optional);
        }
        if (type == double.class || type == Double.class) {
            return NutProcParameter.floatingPoint(nutParam.value(), optional);
        }
        if (type == String.class) {
            return NutProcParameter.string(nutParam.value(), optional);
        }
        throw new IllegalArgumentException("parameter '" + parameter.getName() + "' is of unsupported type");
    }

    private static Function<String, Object> getConverter(Class<?> converterClass, Class<?> paramType) throws IllegalArgumentException {
        final Method method;
        try {
            method = findConverterMethod(converterClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        if (method.getReturnType() != paramType) {
            throw new IllegalArgumentException("the method '%s' does not return %s".formatted(method, paramType));
        }
        return o -> invokeConvertMethod(method, o);
    }

    private static Object invokeConvertMethod(Method method, Object arg) {
        try {
            return method.invoke(arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method findConverterMethod(Class<?> converterClass) throws NoSuchMethodException {
        return Arrays.stream(converterClass.getDeclaredMethods())
                .filter(m -> m.getDeclaredAnnotation(NutConvert.class) != null)
                .findFirst()
                .orElse(converterClass.getDeclaredMethod("parse", String.class));
    }
}
