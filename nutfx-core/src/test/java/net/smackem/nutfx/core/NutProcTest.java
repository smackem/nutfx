package net.smackem.nutfx.core;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
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
    }

    @NutMethod
    void methodWithOptionalParameters(
            @NutParam("n") Integer n,
            @NutParam("b") Boolean b,
            @NutParam("d") Double d,
            @NutParam("s") String s,
            @NutParam(value = "required", isRequired = true) String required) {
    }
}