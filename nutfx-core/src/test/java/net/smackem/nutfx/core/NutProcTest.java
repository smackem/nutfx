package net.smackem.nutfx.core;

import org.junit.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NutProcTest {

    @Test
    public void testNoParameters() {
        final var method = NutTests.getMethodByName(this, "methodWithoutParameters");
        final var proc = NutProc.fromMethod(method);
        assertThat(proc.name()).isEqualTo(method.getName());
        assertThat(proc.parameters()).isEmpty();
        assertThat(proc.method()).isEqualTo(method);
    }

    @NutMethod
    void methodWithoutParameters() {
    }

    @Test
    public void testIllegalReturnType() {
        final var method = NutTests.getMethodByName(this, "methodReturningInt");
        assertThatThrownBy(() -> NutProc.fromMethod(method)).isInstanceOf(IllegalArgumentException.class);
    }

    @NutMethod
    int methodReturningInt() {
        return 0;
    }

    @Test
    public void testBasicParameters() {
        final var method = NutTests.getMethodByName(this, "methodWithBasicParameters");
        final var proc = NutProc.fromMethod(method);
        assertThat(proc.parameters()).hasSize(3);
        assertThat(proc.parameters())
                .extracting(NutProcParameter::name)
                .containsExactly("n", "b", "d");
        assertThat(proc.parameters())
                .extracting(NutProcParameter::type)
                .containsExactly(ParameterType.INTEGER, ParameterType.BOOLEAN, ParameterType.DOUBLE);
        assertThat(proc.parameters())
                .extracting(NutProcParameter::isOptional)
                .containsExactly(false, false, false);
    }

    @NutMethod
    void methodWithBasicParameters(
            @NutParam("n") int n,
            // primitive parameters are ALWAYS required, regardless of the annotation setting
            @NutParam(value = "b", isRequired = false) boolean b,
            @NutParam("d") double d) {
    }

    @Test
    public void testOptionalParameters() {
        final var method = NutTests.getMethodByName(this, "methodWithOptionalParameters");
        final var proc = NutProc.fromMethod(method);
        assertThat(proc.parameters())
                .extracting(NutProcParameter::name)
                .containsExactly("n", "b", "d", "s", "required");
        assertThat(proc.parameters())
                .extracting(NutProcParameter::isOptional)
                .containsExactly(true, true, true, true, false);
        assertThat(proc.parameters())
                .extracting(NutProcParameter::type)
                .containsExactly(ParameterType.INTEGER, ParameterType.BOOLEAN, ParameterType.DOUBLE,
                        ParameterType.STRING, ParameterType.STRING);
    }

    @NutMethod
    void methodWithOptionalParameters(@NutParam("n") Integer n,
                                      @NutParam("b") Boolean b,
                                      @NutParam("d") Double d,
                                      @NutParam("s") String s,
                                      @NutParam(value = "required", isRequired = true) String required) {
    }

    @Test
    public void throwsOnMissingParameterAnnotation() {
        final var method = NutTests.getMethodByName(this, "methodWithMissingParameterAnnotation");
        assertThatThrownBy(() -> NutProc.fromMethod(method)).isInstanceOf(IllegalArgumentException.class);
    }

    @NutMethod
    void methodWithMissingParameterAnnotation(
            @NutParam("n") Integer n,
            String required) {
    }

    @Test
    public void throwsOnAmbiguousParameterName() {
        final var method = NutTests.getMethodByName(this, "methodWithAmbiguousParameterNames");
        assertThatThrownBy(() -> NutProc.fromMethod(method)).isInstanceOf(IllegalArgumentException.class);
    }

    @NutMethod
    void methodWithAmbiguousParameterNames(@NutParam("x") int x, @NutParam("x") int y) {
    }

    @Test
    public void throwsOnUnsupportedParameterType() {
        final var method = NutTests.getMethodByName(this, "methodWithUnsupportedParameterType");
        assertThatThrownBy(() -> NutProc.fromMethod(method)).isInstanceOf(IllegalArgumentException.class);
    }

    @NutMethod
    void methodWithUnsupportedParameterType(@NutParam("x") int x, @NutParam("x") Void y) {
    }

    @Test
    public void customConverter() {
        final var method = NutTests.getMethodByName(this, "methodWithCustomParameter");
        final var proc = NutProc.fromMethod(method);
        assertThat(proc.parameters()).extracting(NutProcParameter::converter)
                .noneMatch(Objects::isNull);
        assertThat(proc.parameters().iterator().next().converter().apply("100")).isEqualTo(100);
    }

    @Test
    public void throwsOnConverterWithoutParseMethod() {
        final var method = NutTests.getMethodByName(this, "methodWithCustomParameterAnnotation");
        assertThatThrownBy(() -> NutProc.fromMethod(method)).isInstanceOf(IllegalArgumentException.class);
    }

    @NutMethod
    void methodWithCustomParameter(@NutParam(value = "x", converterClass = IntegerConverter.class) int x) {
    }

    @NutMethod
    void methodWithCustomParameterAnnotation(@NutParam(value = "x", converterClass = IntegerConverterWithoutParseMethod.class) int x) {
    }

    private static class IntegerConverter {
        static int parse(String s) {
            return Integer.parseInt(s);
        }
    }

    private static class IntegerConverterWithoutParseMethod {
        static int parseInt(String s) {
            return Integer.parseInt(s);
        }
    }
}