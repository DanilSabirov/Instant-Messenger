package client.gui;

import client.AuthenticationData;
import client.Client;
import client.Main;
import client.gui.view.RegistrationWindow;
import client.user.User;
import client.user.UserIM;
import javafx.fxml.FXMLLoader;

import java.util.Arrays;

public class RegistrationController {
    private Client model;

    private RegistrationWindow window;

    public RegistrationController(Client model) {
        this.model = model;
        this.window = new RegistrationWindow(model, this);
    }

    public void register() {
        boolean isAccepted = true;
        window.setIncorrectAll(false);

        String login = window.getLogin();
        if (login.length() <  3){
            window.setIncorrectLogin(true);
            isAccepted = false;
        }

        char[] passwordFirst = window.getPasswordFirst();
        if (passwordFirst.length < 6){
            window.setIncorrectPasswordFirst(true);
            isAccepted = false;
        }

        char[] passwordSecond = window.getPasswordSecond();
        if (!Arrays.equals(passwordFirst, passwordSecond)){
            window.setIncorrectPasswordSecond(true);
            isAccepted = false;
        }

        String name = window.getName();
        if (name.length() < 3) {
            window.setIncorrectName(true);
            isAccepted = false;
        }

        String email = window.getEmail();

        if (isAccepted){
            if (model.register(new UserIM(-1, name, email), new AuthenticationData(login, passwordFirst))){
                Main.setAuthorizationScene();
            }
        }
        return;
    }

    public void cancel() {
        Main.setAuthorizationScene();
    }

    public FXMLLoader getLoader() {
        return window.load();
    }
}
