package net.smackem.nutfx.core;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

public class NutInvocationTest {

    @Test
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        final var controller = new Controller();
        final var method = NutTests.getMethodByName(controller, "methodMutatingSomeValue");
        final var proc = NutProc.fromMethod(method);
        final var invocation = new NutInvocation(proc);
        invocation.put("value", 100);
        invocation.invoke(controller);
        assertThat(controller.someValue).isEqualTo(100);
    }

    private static class Controller {
        int someValue;

        @NutMethod
        void methodMutatingSomeValue(@NutParam("value") int value) {
            this.someValue = value;
        }
    }
}
