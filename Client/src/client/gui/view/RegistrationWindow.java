package client.gui.view;

import client.Client;
import client.gui.RegistrationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;

public class RegistrationWindow {
    @FXML
    TextField login;

    @FXML
    TextField passwordFirst;

    @FXML
    TextField passwordSecond;

    @FXML
    TextField name;

    @FXML
    TextField email;

    @FXML
    Button registerButton;

    @FXML
    Button cancelButton;

    Client model;

    RegistrationController controller;

    public RegistrationWindow(Client model, RegistrationController controller) {
        this.model = model;
        this.controller = controller;
    }

    public FXMLLoader load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/registrationWindow.fxml"));
        loader.setController(this);
        return loader;
    }

    public String getLogin() {
        return login.getText();
    }

    public void setIncorrectLogin(boolean val) {
        login.setStyle(val ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    public void setIncorrectName(boolean val) {
        name.setStyle(val ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    public void setIncorrectPasswordFirst(boolean val) {
        passwordFirst.setStyle(val ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    public void setIncorrectPasswordSecond(boolean val) {
        passwordSecond.setStyle(val ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    public void setIncorrectEmail(boolean val) {
        email.setStyle(val ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    public void setIncorrectAll(boolean val) {
        setIncorrectName(val);
        setIncorrectEmail(val);
        setIncorrectLogin(val);
        setIncorrectPasswordFirst(val);
        setIncorrectPasswordSecond(val);
    }

    public char[] getPasswordFirst() {
        return  passwordFirst.getText().toCharArray();
    }

    public char[] getPasswordSecond() {
        return  passwordSecond.getText().toCharArray();
    }

    public String getName() {
        return name.getText();
    }

    public String getEmail() {
        return email.getText();
    }

    @FXML
    private void register(ActionEvent actionEvent) {
        controller.register();
    }

    @FXML
    private void cancel(ActionEvent actionEvent) {
        controller.cancel();
    }
}
