package net.smackem.nutfx.core;

import net.smackem.nutfx.lang.NutLexer;
import net.smackem.nutfx.lang.NutParser;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NutProcParser {
    private final Map<String, NutProc> procMap;

    public NutProcParser(Object controller) {
        Objects.requireNonNull(controller);
        this.procMap = Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(m -> m.getDeclaredAnnotation(NutMethod.class) != null)
                .map(NutProc::fromMethod)
                .collect(Collectors.toUnmodifiableMap(NutProc::name, proc -> proc));
    }

    public NutInvocation parse(String source) {
        Objects.requireNonNull(source);
        final var lexer = new NutLexer(CharStreams.fromString(source));
        final var tokens = new BufferedTokenStream(lexer);
        final var parser = new NutParser(tokens);
        final var emitter = new NutEmittingVisitor(this.procMap);
        emitter.visitNutProc(parser.nutProc());
        return emitter.invocation();
    }
}
