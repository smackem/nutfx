package net.smackem.nutfx.core;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

public class CommandParser {
    public Command parse(String source) {
        final var lexer = new NutLexer(CharStreams.fromString(source));
        final var tokens = new BufferedTokenStream(lexer);
        final var parser = new NutParser(tokens);
        final var emitter = new NutEmittingVisitor();
        emitter.visitCommand(parser.command());
        return emitter.buildCommand();
    }
}
