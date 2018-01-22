package client.gui;

import client.AuthenticationData;
import client.Client;
import client.Main;
import client.gui.view.AuthorizationWindow;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class AuthorizationController {
    private Client model;

    private AuthorizationWindow window;

    public AuthorizationController(Client model) {
        this.model = model;
        window = new AuthorizationWindow(model, this);
    }

    public FXMLLoader getLoader(){
        return window.load();
    }

    public void logIn(){
        String login = window.getLogit();
        char[] password = window.getPassword();
        if(login.length() != 0 && password.length != 0){
            window.setDisableButton(true);
            if (model.authenticate(new AuthenticationData(login, password)) == -1){
                System.out.println("Accept");
                Main.setMainScene();
            }
            window.setDisableButton(false);
        }
    }

    public void register(){

    }
}
