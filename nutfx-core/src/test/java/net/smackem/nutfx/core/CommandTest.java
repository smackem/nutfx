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
                .with(Parameter.of("customInt", Integer::parseInt))
                .handle(ctx -> {
                    final String name = ctx.getString("name");
                    final double x = ctx.getFloatingPoint("x");
                    final Integer n = ctx.get("customInt");
                    System.out.printf("%s: %f, %d", name, x, n);
                })
                .build();
    }
}