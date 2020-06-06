package net.smackem.nutfx.core;

public class NutEmittingVisitor extends NutBaseVisitor<Void> {
    private Command.Builder builder;

    @Override
    public Void visitCommand(NutParser.CommandContext ctx) {
        this.builder = new Command.Builder(ctx.Ident().getText());
        return super.visitCommand(ctx);
    }

    public Command buildCommand() {
        return this.builder.build();
    }
}
