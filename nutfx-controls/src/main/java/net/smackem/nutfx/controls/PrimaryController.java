package net.smackem.nutfx.controls;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.smackem.nutfx.core.NutMethod;
import net.smackem.nutfx.core.NutParam;
import net.smackem.nutfx.core.NutProc;
import net.smackem.nutfx.core.NutProcParser;

public class PrimaryController {

    private final NutProcParser parser;

    @FXML
    private AutoCompleteTextField<String> nutInputText;

    @FXML
    private ComboBox<String> nutInput;

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
        this.nutInput.getEditor().textProperty().addListener(this::onNutInputChanging);
        this.nutInputText.getEntries().addAll(this.parser.nutProcs().values().stream()
                .map(NutProc::toString)
                .collect(Collectors.toList()));
    }

    private void onNutInputChanging(ObservableValue<? extends String> observable, String old, String val) {
        if (val.isBlank()) {
            this.nutInput.hide();
            return;
        }
        final var tokens = val.split("\\s");
        final var matchingProcs = this.parser.nutProcs().values().stream()
                .filter(proc -> proc.name().startsWith(tokens[0]))
                .map(NutProc::toString)
                .collect(Collectors.toList());
        this.nutInput.setItems(FXCollections.observableList(matchingProcs));
        if (matchingProcs.isEmpty()) {
            this.nutInput.hide();
        } else {
            this.nutInput.show();
        }
    }

    @FXML
    private void handleNutInput(ActionEvent actionEvent) throws InvocationTargetException, IllegalAccessException {
        final var source = this.nutInput.getEditor().getText();
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
