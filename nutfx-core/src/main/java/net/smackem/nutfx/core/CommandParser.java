package net.smackem.nutfx.core;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandParser {
    private final Map<String, Command> availableCommands;

    public CommandParser(Collection<Command> availableCommands) {
        this.availableCommands = availableCommands.stream()
                .collect(Collectors.toUnmodifiableMap(Command::name, command -> command));
    }

    public Command parse(String source) {
        final var lexer = new NutLexer(CharStreams.fromString(source));
        final var tokens = new BufferedTokenStream(lexer);
        final var parser = new NutParser(tokens);
        final var emitter = new NutEmittingVisitor();
        emitter.visitCommand(parser.command());
        return emitter.buildCommand();
    }
}
