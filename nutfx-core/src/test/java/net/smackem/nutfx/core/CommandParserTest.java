package net.smackem.nutfx.core;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParserTest {

    @Test
    public void parse() {
        final CommandParser parser = new CommandParser(List.of(new Command.Builder("test")
                .with(Parameter.string("paramStr"))
                .with(Parameter.integer("paramInt"))
                .with(Parameter.floatingPoint("paramFloat"))
                .with(Parameter.bool("paramBool"))
                .handle(ctx -> {})
                .build()));
        final CommandContext ctx = parser.parse("test 'paramStrVal' 123 44.5 -paramBool");
        assertThat(ctx.command().name()).isEqualTo("test");
        assertThat(ctx.getString("paramStr")).isEqualTo("paramStrVal");
        assertThat(ctx.getInteger("paramInt")).isEqualTo(123);
        assertThat(ctx.getFloatingPoint("paramFloat")).isEqualTo(44.5);
        assertThat(ctx.getBoolean("paramBool")).isTrue();
    }
}
