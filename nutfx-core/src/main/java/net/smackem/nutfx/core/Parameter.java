package net.smackem.nutfx.core;

import java.util.Objects;

public class Parameter<T> {
    private final String name;
    private final ParameterType type;
    private final T defaultValue;
    private final boolean optional;

    private Parameter(String name, ParameterType type, T defaultValue, boolean optional) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.defaultValue = defaultValue;
        this.optional = optional;
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

    public static Parameter<String> string(String name) {
        return new Parameter<>(name, ParameterType.STRING, null, false);
    }

    public static Parameter<String> string(String name, String defaultValue) {
        return new Parameter<>(name, ParameterType.INTEGER, defaultValue, true);
    }

    public static Parameter<Integer> integer(String name) {
        return new Parameter<>(name, ParameterType.INTEGER, null, false);
    }

    public static Parameter<Integer> integer(String name, int defaultValue) {
        return new Parameter<>(name, ParameterType.INTEGER, defaultValue, true);
    }

    public static Parameter<Boolean> bool(String name) {
        return new Parameter<>(name, ParameterType.BOOLEAN, null, false);
    }

    public static Parameter<Boolean> bool(String name, boolean defaultValue) {
        return new Parameter<>(name, ParameterType.BOOLEAN, defaultValue, true);
    }

    public static Parameter<Double> floatingPoint(String name) {
        return new Parameter<>(name, ParameterType.DOUBLE, null, false);
    }

    public static Parameter<Double> floatingPoint(String name, double defaultValue) {
        return new Parameter<>(name, ParameterType.DOUBLE, defaultValue, true);
    }

    public static <T> Parameter<T> of(String name) {
        return new Parameter<>(name, ParameterType.CUSTOM, null, false);
    }

    public static <T> Parameter<T> of(String name, T defaultValue) {
        return new Parameter<>(name, ParameterType.CUSTOM, defaultValue, true);
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
