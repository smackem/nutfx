package net.smackem.nutfx.controls;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.smackem.nutfx.core.NutMethod;
import net.smackem.nutfx.core.NutProcParser;

public class PrimaryController {

    @FXML
    private TextField nutInput;

    @FXML
    @NutMethod("switch")
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void initialize() {
        this.nutInput.textProperty().addListener(s -> {
            System.out.println(((StringProperty) s).get());
        });
    }

    @FXML
    private void handleNutInput(ActionEvent actionEvent) throws InvocationTargetException, IllegalAccessException {
        final var parser = new NutProcParser(this);
        final var invocation = parser.parse(this.nutInput.getText());
        if (invocation != null) {
            invocation.invoke(this);
        }
    }
}
