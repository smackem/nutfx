package net.smackem.nutfx.core;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NutProcParserTest {

    @Test
    public void takesNameFromAnnotation() throws InvocationTargetException, IllegalAccessException {
        final Controller controller = new Controller();
        final NutProcParser parser = new NutProcParser(controller);
        final String source = """
                test-it
                """;
        final var invocation = parser.parse(source);
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("done");
    }

    @Test
    public void parseMethodWithoutParams() throws InvocationTargetException, IllegalAccessException {
        final Controller controller = new Controller();
        final NutProcParser parser = new NutProcParser(controller);
        final String source = """
                test
                """;
        final var invocation = parser.parse(source);
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("done");
    }

    @Test
    public void parseMethodWithParams() throws InvocationTargetException, IllegalAccessException {
        final Controller controller = new Controller();
        final NutProcParser parser = new NutProcParser(controller);
        final String source = """
                test-params 123 'hello' -b
                """;
        final var invocation = parser.parse(source);
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("123 hello true");
    }

    @Test
    public void throwsOnDuplicateMethodNames() {
        final var controller = new ControllerWithDuplicates();
        assertThatThrownBy(() -> new NutProcParser(controller)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void requiredBooleansDefaultToFalse() throws InvocationTargetException, IllegalAccessException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        final var invocation = parser.parse("require-booleans");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("false false false");
    }

    @Test
    public void optionalBooleansDefaultToNull() throws InvocationTargetException, IllegalAccessException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        final var invocation = parser.parse("opt-booleans");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("false null null");
    }

    @Test
    public void parseBooleans() throws InvocationTargetException, IllegalAccessException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        var invocation = parser.parse("opt-booleans true false true");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("true false true");
        invocation = parser.parse("opt-booleans -b -a=false");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("false true null");
        invocation = parser.parse("opt-booleans -c");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("false null true");
    }

    static class Controller {
        String string;

        @NutMethod
        void test() {
            this.string = "done";
        }

        @NutMethod("test-it")
        void testIt() {
            this.string = "done";
        }

        @NutMethod("test-params")
        void testParams(@NutParam("n") int n,
                        @NutParam("s") String s,
                        @NutParam("b") boolean b) {
            this.string = "%d %s %b".formatted(n, s, b);
        }

        @NutMethod("require-booleans")
        void requireBooleans(@NutParam("a") boolean a,
                  @NutParam("b") boolean b,
                  @NutParam(value = "c", isRequired = true) Boolean c) {
            this.string = "%b %b %b".formatted(a, b, c);
        }

        @NutMethod("opt-booleans")
        void optBooleans(@NutParam("a") boolean a,
                        @NutParam("b") Boolean b,
                        @NutParam("c") Boolean c) {
            this.string = "%b %s %s".formatted(
                    a,
                    b != null ? b.toString() : "null",
                    c != null ? c.toString() : "null");
        }
    }

    static class ControllerWithDuplicates {
        @NutMethod()
        void a() { }

        @NutMethod("a")
        void b() { }
    }
}
