package net.smackem.nutfx.core;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandParser {
    private final Map<String, Command> availableCommands;

    public CommandParser(Collection<Command> availableCommands) {
        this.availableCommands = Objects.requireNonNull(availableCommands).stream()
                .collect(Collectors.toUnmodifiableMap(Command::name, command -> command));
    }

    public CommandContext parse(String source) {
        Objects.requireNonNull(source);
        final var lexer = new NutLexer(CharStreams.fromString(source));
        final var tokens = new BufferedTokenStream(lexer);
        final var parser = new NutParser(tokens);
        final var emitter = new NutEmittingVisitor(this.availableCommands);
        emitter.visitCommand(parser.command());
        return emitter.commandContext();
    }
}
