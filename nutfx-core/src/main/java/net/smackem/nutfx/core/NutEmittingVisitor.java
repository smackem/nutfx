package net.smackem.nutfx.core;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;
import java.util.Objects;

public class NutEmittingVisitor extends NutBaseVisitor<Void> {
    private final Map<String, Command> availableCommands;
    private CommandContext commandContext;
    private int positionalParameterIndex;

    public NutEmittingVisitor(Map<String, Command> availableCommands) {
        this.availableCommands = Objects.requireNonNull(availableCommands);
    }

    public CommandContext commandContext() {
        return this.commandContext;
    }

    @Override
    public Void visitCommand(NutParser.CommandContext ctx) {
        final String ident = ctx.Ident().getText();
        final Command recognizedCommand = this.availableCommands.get(ident);
        if (recognizedCommand == null) {
            logError(ctx, String.format("unrecognized command: %s", ident));
        }
        this.commandContext = new CommandContext(recognizedCommand);
        return super.visitCommand(ctx);
    }

    @Override
    public Void visitPositionalParameter(NutParser.PositionalParameterContext ctx) {
        final var parameters = this.commandContext.command().parameters();
        if (this.positionalParameterIndex >= parameters.size()) {
            logError(ctx, "too many positional parameters");
        } else {
            final var parameter = parameters.get(this.positionalParameterIndex);
            this.positionalParameterIndex++;
            this.commandContext.put(parameter.name(), parseValue(ctx.value(), parameter));
        }
        return super.visitPositionalParameter(ctx);
    }

    @Override
    public Void visitNamedParameter(NutParser.NamedParameterContext ctx) {
        final var parameter = this.commandContext.command().parameters().stream()
                .filter(p -> Objects.equals(p.name(), ctx.Ident().getText()))
                .findFirst()
                .orElse(null);
        if (parameter == null) {
            logError(ctx, "unknown parameter: " + ctx.Ident().getText());
        } else {
            this.commandContext.put(parameter.name(), parseValue(ctx.value(), parameter));
        }
        return super.visitNamedParameter(ctx);
    }

    private Object parseValue(NutParser.ValueContext ctx, Parameter<?> parameter) {
        return switch (parameter.type()) {
            case STRING -> ctx.String().getText().replaceAll("[\"']", "");
            case INTEGER -> Integer.parseInt(ctx.Integer().getText());
            case DOUBLE -> Double.parseDouble(ctx.Float().getText());
            case BOOLEAN -> Boolean.TRUE;
            case CUSTOM -> null;
        };
    }

    private void logError(ParserRuleContext ctx, String message) {
        throw new RuntimeException(String.format("line %d, pos %d: %s",
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                message));
    }
}
