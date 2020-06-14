package net.smackem.nutfx.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NutProcTest {

    @Test
    public void testNoParameters() throws NoSuchMethodException {
        final var method = getClass().getDeclaredMethod("methodWithoutParameters");
        final var proc = NutProc.fromMethod(method);
        assertThat(proc.name()).isEqualTo(method.getName());
        assertThat(proc.parameters()).isEmpty();
        assertThat(proc.method()).isEqualTo(method);
    }

    @NutMethod
    void methodWithoutParameters() {
    }

    @Test
    public void testIllegalReturnType() throws NoSuchMethodException {
        final var method = getClass().getDeclaredMethod("methodReturningInt");
        assertThatThrownBy(() -> NutProc.fromMethod(method)).isInstanceOf(IllegalArgumentException.class);
    }

    @NutMethod
    int methodReturningInt() {
        return 0;
    }

    @Test
    public void testBasicParameters() throws NoSuchMethodException {
        final var method = getClass().getDeclaredMethod("methodWithBasicParameters");
        final var proc = NutProc.fromMethod(method);
        assertThat(proc.parameters()).hasSize(3);
        assertThat(proc.parameters()).extracting(NutProcParameter::name).containsExactly("n", "s", "d");
    }

    @NutMethod
    void methodWithBasicParameters(int n, boolean s, double d) {
    }
}