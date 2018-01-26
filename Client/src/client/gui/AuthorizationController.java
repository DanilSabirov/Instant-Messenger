package client.gui;

import client.AuthenticationData;
import client.Client;
import client.ClientIM;
import client.Main;
import client.gui.view.AuthorizationWindow;
import javafx.fxml.FXMLLoader;

import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorizationController {
    private static Logger log = Logger.getLogger(AuthorizationController.class.getName());

    private Client model;

    private AuthorizationWindow window;

    public AuthorizationController(Client model) {
        this.model = model;
        window = new AuthorizationWindow(model, this);
    }

    public FXMLLoader getLoader(){
        return window.load();
    }

    public void logIn() {
        String login = window.getLogit();
        char[] password = window.getPassword();
        if(login.length() != 0 && password.length != 0){
            window.setDisableButton(true);
            try {
                model.authenticate(new AuthenticationData(login, password));
            } catch (XMLStreamException e) {
                log.log(Level.SEVERE, "Exception: ", e);
            }
            window.setDisableButton(false);
        }
    }

    public void register(){
        Main.setRegistrationScene();
    }
}
