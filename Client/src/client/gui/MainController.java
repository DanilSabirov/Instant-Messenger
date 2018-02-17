package client.gui;

import client.Client;
import client.gui.view.MainWindow;
import javafx.fxml.FXMLLoader;

public class MainController {
    private Client model;

    private MainWindow window;

    public MainController(Client model) {
        this.model = model;
        window = new MainWindow(model, this);
    }

    public FXMLLoader getLoader() {
        return window.load();
    }

    public void sendMessage() {
        String text = window.getText();
        if(text.length() != 0){
     //       model.sendMessage(new UserMessage(1, text));
        }
    }

    public void setUserInfo() {
        window.setUserInfo();
    }

    public void searchUser() {
        if (window.getSearchUser().length() > 0) {
            model.searchUser(window.getSearchUser());
        }
    }

    public void createDialog(int dialogId) {
        model.createDialog(dialogId);

    }
}
