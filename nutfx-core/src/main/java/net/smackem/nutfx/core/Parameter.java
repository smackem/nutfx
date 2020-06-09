package net.smackem.nutfx.core;

import java.util.Objects;
import java.util.function.Function;

public class Parameter<T> {
    private final String name;
    private final ParameterType type;
    private final T defaultValue;
    private final boolean optional;
    private final Function<String, T> converter;

    private Parameter(String name, ParameterType type, T defaultValue, boolean optional, Function<String, T> converter) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.defaultValue = defaultValue;
        this.optional = optional;
        this.converter = converter;
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

    public T defaultValue() {
        return this.defaultValue;
    }

    public Function<String, T> converter() {
        return this.converter;
    }

    public static Parameter<String> string(String name) {
        return new Parameter<>(name, ParameterType.STRING, null, false, null);
    }

    public static Parameter<String> string(String name, String defaultValue) {
        return new Parameter<>(name, ParameterType.INTEGER, defaultValue, true, null);
    }

    public static Parameter<Integer> integer(String name) {
        return new Parameter<>(name, ParameterType.INTEGER, null, false, null);
    }

    public static Parameter<Integer> integer(String name, int defaultValue) {
        return new Parameter<>(name, ParameterType.INTEGER, defaultValue, true, null);
    }

    public static Parameter<Boolean> bool(String name) {
        return new Parameter<>(name, ParameterType.BOOLEAN, null, false, null);
    }

    public static Parameter<Boolean> bool(String name, boolean defaultValue) {
        return new Parameter<>(name, ParameterType.BOOLEAN, defaultValue, true, null);
    }

    public static Parameter<Double> floatingPoint(String name) {
        return new Parameter<>(name, ParameterType.DOUBLE, null, false, null);
    }

    public static Parameter<Double> floatingPoint(String name, double defaultValue) {
        return new Parameter<>(name, ParameterType.DOUBLE, defaultValue, true, null);
    }

    public static <T> Parameter<T> of(String name, Function<String, T> converter) {
        Objects.requireNonNull(converter);
        return new Parameter<>(name, ParameterType.CUSTOM, null, false, converter);
    }

    public static <T> Parameter<T> of(String name, Function<String, T> converter, T defaultValue) {
        Objects.requireNonNull(converter);
        return new Parameter<>(name, ParameterType.CUSTOM, defaultValue, true, converter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Parameter<?> parameter = (Parameter<?>) o;

        if (optional != parameter.optional) return false;
        if (!name.equals(parameter.name)) return false;
        if (type != parameter.type) return false;
        return Objects.equals(defaultValue, parameter.defaultValue);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (optional ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", defaultValue=" + defaultValue +
               ", optional=" + optional +
               '}';
    }
}
