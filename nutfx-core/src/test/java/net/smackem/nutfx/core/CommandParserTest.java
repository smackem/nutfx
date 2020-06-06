package net.smackem.nutfx.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParserTest {

    @Test
    public void parse() {
        final CommandParser parser = new CommandParser();
        final Command command = parser.parse("hello");
        assertThat(command.name()).isEqualTo("hello");
    }
}
