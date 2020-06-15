package net.smackem.nutfx.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

class NutTests {
    private NutTests() {
        throw new IllegalAccessError();
    }

    public static Method getMethodByName(Object obj, String methodName) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(m -> Objects.equals(m.getName(), methodName))
                .findFirst()
                .orElseThrow();
    }
}
