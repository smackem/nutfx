package net.smackem.nutfx.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NutInvocation {
    private final Map<String, Object> arguments = new HashMap<>();
    private final NutProc proc;

    NutInvocation(NutProc proc) {
        this.proc = proc;
    }

    NutProc proc() {
        return this.proc;
    }

    public void invoke() {
        final Object[] args = new Object[this.proc.parameters().size()];
    }

    void put(String name, Object value) {
        this.arguments.put(name, value);
    }

    String getString(String name) {
        return (String) this.arguments.get(name);
    }

    int getInteger(String name) {
        return (Integer) this.arguments.get(name);
    }

    double getFloatingPoint(String name) {
        return (Double) this.arguments.get(name);
    }

    boolean getBoolean(String name) {
        return Objects.equals(this.arguments.get(name), Boolean.TRUE);
    }

    <T> T get(String name) {
        //noinspection unchecked
        return (T) this.arguments.get(name);
    }
}
