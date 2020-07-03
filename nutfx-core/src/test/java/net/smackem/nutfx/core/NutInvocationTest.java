package net.smackem.nutfx.core;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    public void throwsOnMissingParam() throws InvocationTargetException, IllegalAccessException {
        final var controller = new Controller();
        final var method = NutTests.getMethodByName(controller, "methodWithRequiredNullableParam");
        final var proc = NutProc.fromMethod(method);
        final var invocation = new NutInvocation(proc);
        // dont put any arguments
        assertThatThrownBy(() -> invocation.invoke(controller)).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    public void acceptsMissingOptionalParams() throws InvocationTargetException, IllegalAccessException {
        final var controller = new Controller();
        final var method = NutTests.getMethodByName(controller, "methodWithOptionalParams");
        final var proc = NutProc.fromMethod(method);
        final var invocation = new NutInvocation(proc);
        invocation.put("n", 666);
        invocation.invoke(controller);
        assertThat(controller.someValue).isEqualTo(666);
    }

    private static class Controller {
        int someValue;

        @NutMethod
        void methodMutatingSomeValue(@NutParam("value") int value) {
            this.someValue = value;
        }

        @NutMethod
        void methodWithRequiredNullableParam(@NutParam(value = "value", isRequired = true) String value) {
            System.out.println(value);
        }

        @NutMethod
        void methodWithOptionalParams(@NutParam("s") String s,
                                      @NutParam("n") Integer n,
                                      @NutParam("b") Boolean b) {
            System.out.printf("%s %d %b", s, n, b);
            this.someValue = n != null ? n : 0;
        }
    }
}
