package net.smackem.nutfx.core;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {
    private final Map<String, Object> arguments = new HashMap<>();

    void put(String name, Object value) {
        this.arguments.put(name, value);
    }

    public String getString(String name) {
        return (String) this.arguments.get(name);
    }

    public int getInteger(String name) {
        return (Integer) this.arguments.get(name);
    }

    public double getFloatingPoint(String name) {
        return (Double) this.arguments.get(name);
    }

    public <T> T get(String name) {
        //noinspection unchecked
        return (T) this.arguments.get(name);
    }
}
