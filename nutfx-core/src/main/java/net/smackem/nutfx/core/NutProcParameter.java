package net.smackem.nutfx.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class NutProcParameter<T> {
    private final String name;
    private final ParameterType type;
    private final boolean optional;
    private final Function<String, T> converter;
    private final Collection<T> possibleValues;

    private NutProcParameter(String name, ParameterType type, boolean optional, Function<String, T> converter,
                             Collection<T> possibleValues) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.optional = optional;
        this.converter = converter;
        this.possibleValues = possibleValues;
    }

    public String name() {
        return name;
    }

    public ParameterType type() {
        return type;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public Function<String, T> converter() {
        return this.converter;
    }

    public Collection<T> possibleValues() {
        return this.possibleValues;
    }

    static NutProcParameter<String> string(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.STRING, optional, null, null);
    }

    static NutProcParameter<Integer> integer(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.INTEGER, optional, null, null);
    }

    static NutProcParameter<Boolean> bool(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.BOOLEAN, optional, null, null);
    }

    static NutProcParameter<Double> float64(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.DOUBLE, optional, null, null);
    }

    static <T> NutProcParameter<T> enumeration(String name, Class<T> enumClass, boolean optional) {
        if (enumClass.isEnum() == false) {
            throw new IllegalArgumentException("specified class '" + enumClass + "' is not an enum");
        }
        final Method convertMethod;
        try {
            convertMethod = enumClass.getDeclaredMethod("valueOf", String.class);
        } catch (NoSuchMethodException impossible) {
            throw new RuntimeException("enum without valueOf");
        }
        return new NutProcParameter<>(
                name,
                ParameterType.ENUM,
                optional,
                s -> invokeConvertMethod(convertMethod, s),
                List.of(enumClass.getEnumConstants()));
    }

    static <T> NutProcParameter<T> custom(String name, Function<String, T> converter, boolean optional) {
        Objects.requireNonNull(converter);
        return new NutProcParameter<>(name, ParameterType.CUSTOM, optional, converter, null);
    }

    private static <T> T invokeConvertMethod(Method method, String arg) {
        try {
            //noinspection unchecked
            return (T) method.invoke(null, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NutProcParameter<?> that = (NutProcParameter<?>) o;

        if (optional != that.optional) return false;
        if (!name.equals(that.name)) return false;
        if (type != that.type) return false;
        return Objects.equals(converter, that.converter);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (optional ? 1 : 0);
        result = 31 * result + (converter != null ? converter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NutProcParameter{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", optional=" + optional +
               ", converter=" + converter +
               '}';
    }
}
