package net.smackem.nutfx.core;

import javafx.scene.input.KeyCodeCombination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

public class Command {
    private final String name;
    private final KeyCodeCombination shortcut;
    private final Collection<Parameter<?>> parameters;
    private final Consumer<CommandContext> handler;

    private Command(Builder builder) {
        this.name = builder.name;
        this.shortcut = builder.shortcut;
        this.parameters = Collections.unmodifiableCollection(builder.parameters);
        this.handler = builder.handler;
    }

    public static class Builder {
        private final String name;
        private KeyCodeCombination shortcut;
        private final Collection<Parameter<?>> parameters = new ArrayList<>();
        private Consumer<CommandContext> handler;

        public Builder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public Builder shortcut(KeyCodeCombination value) {
            this.shortcut = value;
            return this;
        }

        public Builder with(Parameter<?> parameter) {
            this.parameters.add(Objects.requireNonNull(parameter));
            return this;
        }

        public Builder handle(Consumer<CommandContext> value) {
            this.handler = Objects.requireNonNull(value);
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }

    public String name() {
        return this.name;
    }

    public KeyCodeCombination shortcut() {
        return this.shortcut;
    }

    public Collection<Parameter<?>> parameters() {
        return this.parameters;
    }

    public Consumer<CommandContext> handler() {
        return this.handler;
    }
}
