package client.gui;

import client.Client;
import client.gui.view.MainWindow;
import client.message.UserMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javax.xml.stream.XMLStreamException;
import java.time.ZonedDateTime;

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
        if(text.length() != 0 && window.getIndexCurDialog() != -1){
            try {
                System.out.println(model.getDialogs().get(window.getIndexCurDialog()).getId());
                model.sendMessage(new UserMessage(model.getUser().getId(), model.getUser().getName(), model.getDialogs().get(window.getIndexCurDialog()).getId(), window.getText(), ZonedDateTime.now()));
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        window.clearText();
    }

    public void searchUser() {
        if (window.getSearchUser().length() > 0) {
            window.clearFoundUserList();
            model.searchUser(window.getSearchUser());
            window.setFoundUsersList();
            window.clearSearch();
        }
    }

    public void createDialog(int dialogId) {
        model.createDialog(dialogId);
        window.setDialogList();
        window.clearFoundUserList();
    }

    public void initWindow() {
        window.init();
        window.setUserInfo();
    }
}
