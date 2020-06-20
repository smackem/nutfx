package net.smackem.nutfx.core;

import net.smackem.nutfx.lang.NutBaseVisitor;
import net.smackem.nutfx.lang.NutParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;
import java.util.Objects;

public class NutEmittingVisitor extends NutBaseVisitor<Void> {
    private final Map<String, NutProc> availableCommands;
    private NutInvocation invocation;
    private int positionalParameterIndex;

    public NutEmittingVisitor(Map<String, NutProc> availableCommands) {
        this.availableCommands = Objects.requireNonNull(availableCommands);
    }

    public NutInvocation invocation() {
        return this.invocation;
    }

    @Override
    public Void visitNutProc(NutParser.NutProcContext ctx) {
        final String ident = ctx.Ident().getText();
        final NutProc recognizedNutProc = this.availableCommands.get(ident);
        if (recognizedNutProc == null) {
            logError(ctx, String.format("unrecognized command: %s", ident));
        }
        this.invocation = new NutInvocation(recognizedNutProc);
        return super.visitNutProc(ctx);
    }

    @Override
    public Void visitPositionalParameter(NutParser.PositionalParameterContext ctx) {
        final var parameters = this.invocation.proc().parameters();
        if (this.positionalParameterIndex >= parameters.size()) {
            logError(ctx, "too many positional parameters");
        } else {
            final var parameter = parameters.get(this.positionalParameterIndex);
            this.positionalParameterIndex++;
            this.invocation.put(parameter.name(), parseValue(ctx.value(), parameter));
        }
        return super.visitPositionalParameter(ctx);
    }

    @Override
    public Void visitNamedParameter(NutParser.NamedParameterContext ctx) {
        final var parameter = this.invocation.proc().parameters().stream()
                .filter(p -> Objects.equals(p.name(), ctx.Ident().getText()))
                .findFirst()
                .orElse(null);
        if (parameter == null) {
            logError(ctx, "unknown parameter: " + ctx.Ident().getText());
        } else {
            this.invocation.put(parameter.name(), parseValue(ctx.value(), parameter));
        }
        return super.visitNamedParameter(ctx);
    }

    private Object parseValue(NutParser.ValueContext ctx, NutProcParameter<?> parameter) {
        return switch (parameter.type()) {
            case STRING -> ctx.getText().replaceAll("^[\"']|[\"']$", "");
            case INTEGER -> Integer.parseInt(ctx.Integer().getText());
            case DOUBLE -> Double.parseDouble(ctx.Float().getText());
            case BOOLEAN -> ctx == null || ctx.getText().isBlank() || Boolean.parseBoolean(ctx.getText());
            case CUSTOM -> parameter.converter().apply(ctx.getText());
        };
    }

    private void logError(ParserRuleContext ctx, String message) {
        throw new RuntimeException(String.format("line %d, pos %d: %s",
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                message));
    }
}
