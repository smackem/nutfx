package net.smackem.nutfx.core;

import net.smackem.nutfx.lang.NutLexer;
import net.smackem.nutfx.lang.NutParser;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NutProcParser {
    private final Map<String, NutProc> procMap;

    public NutProcParser(Object controller) {
        Objects.requireNonNull(controller);
        this.procMap = Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(NutMethod.class))
                .map(NutProc::fromMethod)
                .collect(Collectors.toMap(NutProc::name, proc -> proc));
    }

    public Map<String, NutProc> nutProcs() {
        return Collections.unmodifiableMap(this.procMap);
    }

    public NutProc createAlias(NutProc proc, String aliasName) {
        Objects.requireNonNull(proc);
        if (this.procMap.get(proc.name()) != proc) {
            throw new IllegalArgumentException("the given proc is not present in this parser's context!");
        }
        if (this.procMap.containsKey(aliasName)) {
            throw new IllegalArgumentException("there already exists a proc with the given aliasName");
        }
        final NutProc alias = proc.alias(aliasName);
        this.procMap.put(aliasName, alias);
        return alias;
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
