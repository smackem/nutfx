package net.smackem.nutfx.core;

import org.junit.Test;

public class CommandTest {
    @Test
    public void testCommand() {
        final var command = new Command.Builder("newRobot")
                .with(Parameter.string("name"))
                .with(Parameter.string("color", "#ffffff"))
                .with(Parameter.floatingPoint("x"))
                .with(Parameter.floatingPoint("y"))
                .with(Parameter.<Command>of("gurke"))
                .handle(ctx -> {
                    final var name = ctx.getString("name");
                    final var x = ctx.getFloatingPoint("x");
                    System.out.printf("%s: %f", name, x);
                })
                .build();
    }
}