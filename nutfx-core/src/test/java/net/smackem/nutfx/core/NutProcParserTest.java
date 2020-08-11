package net.smackem.nutfx.core;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NutProcParserTest {

    @Test
    public void takesNameFromAnnotation() throws InvocationTargetException {
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
    public void parseMethodWithoutParams() throws InvocationTargetException {
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
    public void parseMethodWithParams() throws InvocationTargetException {
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
    public void requiredBooleansDefaultToFalse() throws InvocationTargetException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        final var invocation = parser.parse("require-booleans");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("false false false");
    }

    @Test
    public void optionalBooleansDefaultToNull() throws InvocationTargetException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        final var invocation = parser.parse("opt-booleans");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("false null null");
    }

    @Test
    public void parseBooleans() throws InvocationTargetException {
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


    @Test
    public void testCustomConverter() throws InvocationTargetException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        var invocation = parser.parse("get-pointmag -point='100;150'");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("250");
    }

    @Test
    public void testEnum() throws InvocationTargetException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        var invocation = parser.parse("get-html BODY");
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("BODY");
    }

    @Test
    public void createAlias() throws InvocationTargetException {
        final var controller = new Controller();
        final var parser = new NutProcParser(controller);
        final var alias = parser.createAlias(parser.nutProcs().get("test-it"), "alias");
        final var invocation = parser.parse("alias");
        assertThat(invocation.proc()).isSameAs(alias);
        invocation.invoke(controller);
        assertThat(controller.string).isEqualTo("done");
        assertThatThrownBy(() -> parser.createAlias(alias, "alias")).isInstanceOf(IllegalArgumentException.class);
    }

    private static class Controller {
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

        @NutMethod("get-pointmag")
        void getPointMagnitude(@NutParam("point") Point p) {
            this.string = String.valueOf(p.x() + p.y());
        }

        @NutMethod("get-html")
        void getHtml(@NutParam(value = "html", isRequired = true) HtmlTag html) {
            this.string = html.toString();
        }
    }

    private static class ControllerWithDuplicates {
        @NutMethod()
        void a() { }

        @NutMethod("a")
        void b() { }
    }

    private record Point(int x, int y) {
        static Point parse(String s) {
            final String[] tokens = s.split(";");
            return new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
        }
    }

    private enum HtmlTag {
        A,
        DIV,
        HEAD,
        BODY,
        HTML,
    }
}
