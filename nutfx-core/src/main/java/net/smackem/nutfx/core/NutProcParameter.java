package net.smackem.nutfx.core;

import java.util.Objects;
import java.util.function.Function;

class NutProcParameter<T> {
    private final String name;
    private final ParameterType type;
    private final boolean optional;
    private final Function<String, T> converter;

    private NutProcParameter(String name, ParameterType type, boolean optional, Function<String, T> converter) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
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

    public Function<String, T> converter() {
        return this.converter;
    }

    public static NutProcParameter<String> string(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.INTEGER, optional, null);
    }

    public static NutProcParameter<Integer> integer(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.INTEGER, optional, null);
    }

    public static NutProcParameter<Boolean> bool(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.BOOLEAN, optional, null);
    }

    public static NutProcParameter<Double> floatingPoint(String name, boolean optional) {
        return new NutProcParameter<>(name, ParameterType.DOUBLE, optional, null);
    }

    public static <T> NutProcParameter<T> of(String name, Function<String, T> converter, boolean optional) {
        Objects.requireNonNull(converter);
        return new NutProcParameter<>(name, ParameterType.CUSTOM, optional, converter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NutProcParameter<?> parameter = (NutProcParameter<?>) o;

        if (optional != parameter.optional) return false;
        if (!name.equals(parameter.name)) return false;
        return type == parameter.type;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (optional ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", optional=" + optional +
               '}';
    }
}
