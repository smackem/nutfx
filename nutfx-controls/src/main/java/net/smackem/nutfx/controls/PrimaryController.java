package net.smackem.nutfx.controls;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import net.smackem.nutfx.core.NutMethod;
import net.smackem.nutfx.core.NutParam;
import net.smackem.nutfx.core.NutProcParser;

public class PrimaryController {

    private final NutProcParser parser;

    @FXML
    private NutTextField nutInputText;

    public PrimaryController() {
        this.parser = new NutProcParser(this);
    }

    @FXML
    @NutMethod("switch")
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void initialize() {
        this.nutInputText.getEntries().addAll(this.parser.nutProcs().values());
    }

    @FXML
    private void handleNutInput(ActionEvent actionEvent) throws InvocationTargetException, IllegalAccessException {
        final var source = this.nutInputText.getText();
        if (source.isBlank()) {
            return;
        }
        final var invocation = parser.parse(source);
        if (invocation != null) {
            invocation.invoke(this);
        }
    }

    @NutMethod
    private void doIt() {
    }

    @NutMethod
    private void draw(@NutParam("color") String color) {
    }

    @NutMethod("new")
    private void newRobot(@NutParam("name") String name) {
    }
}
