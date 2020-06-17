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

    static class Controller {
        String string;

        @NutMethod()
        void test() {
            this.string = "done";
        }

        @NutMethod("test-it")
        void testIt() {
            this.string = "done";
        }

        @NutMethod("test-params")
        void voidTestParams(@NutParam("n") int n, @NutParam("s") String s, @NutParam("b") boolean b) {
            this.string = "%d %s %b".formatted(n, s, b);
        }
    }

    static class ControllerWithDuplicates {
        @NutMethod()
        void a() { }

        @NutMethod("a")
        void b() { }
    }
}
