package net.smackem.nutfx.core;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NutInvocation {
    private final Map<String, Object> arguments = new HashMap<>();
    private final NutProc proc;

    NutInvocation(NutProc proc) {
        this.proc = proc;
        this.proc.parameters().stream()
                .filter(p -> p.type() == ParameterType.BOOLEAN && p.isOptional() == false)
                .forEach(p -> put(p.name(), false));
        this.proc.method().setAccessible(true);
    }

    NutProc proc() {
        return this.proc;
    }

    public void invoke(Object controller) throws InvocationTargetException, IllegalAccessException {
        Objects.requireNonNull(controller);
        final Object[] args = new Object[this.proc.parameters().size()];
        int index = 0;
        for (final var parameter : this.proc.parameters()) {
            final Object arg = this.arguments.get(parameter.name());
            if (arg == null && parameter.isOptional() == false) {
                throw new InvocationTargetException(
                        new UnsupportedOperationException("NutMethod '%s': NutParam '%s' is required but has value null"
                                .formatted(this.proc.name(), parameter.name())));
            }
            args[index++] = arg;
        }
        this.proc.method().invoke(controller, args);
    }

    void put(String name, Object value) {
        this.arguments.put(name, value);
    }

    Object get(String name) {
        return this.arguments.get(name);
    }
}
