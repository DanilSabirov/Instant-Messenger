package client.gui.view;

import client.Client;
import client.gui.AuthorizationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthorizationWindow {
    @FXML
    Button logInButton;

    @FXML
    Button registerButton;

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    private Client model;

    private AuthorizationController controller;

    public AuthorizationWindow(Client model, AuthorizationController controller) {
        this.model = model;
        this.controller = controller;
    }

    public FXMLLoader load(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/authorizationWindow.fxml"));
        loader.setController(this);
        return loader;
    }

    public void setDisableButton(boolean value){
        logInButton.setDisable(value);
        registerButton.setDisable(value);
    }

    public String getLogit(){
        return login.getText();
    }

    public char[] getPassword(){
        return password.getText().toCharArray();
    }

    @FXML
    private void logIn(ActionEvent actionEvent){
        controller.logIn();
    }

    @FXML
    private void register(ActionEvent actionEvent){
        controller.register();
    }

}
