package net.smackem.nutfx.core;

import java.util.Map;
import java.util.Objects;

public class NutEmittingVisitor extends NutBaseVisitor<Void> {
    private final Map<String, Command> availableCommands;
    private CommandContext commandContext;

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
            throw new RuntimeException(String.format("unrecognized command: %s", ident));
        }
        this.commandContext = new CommandContext(recognizedCommand);
        return super.visitCommand(ctx);
    }

    @Override
    public Void visitPositionalParameter(NutParser.PositionalParameterContext ctx) {
        return super.visitPositionalParameter(ctx);
    }

    @Override
    public Void visitNamedParameter(NutParser.NamedParameterContext ctx) {
        return super.visitNamedParameter(ctx);
    }
}
