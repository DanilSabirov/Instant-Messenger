package client.gui.view;

import client.Client;
import client.gui.MainController;
import client.message.UserMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


public class MainWindow {
    @FXML
    ListView dialogs;

    @FXML
    ListView messages;

    @FXML
    TextArea text;

    @FXML
    Button send;

    private Client model;

    private MainController controller;

    public MainWindow(Client model, MainController controller) {
        this.model = model;
        this.controller = controller;
    }

    public FXMLLoader load(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/mainWindow.fxml"));
        loader.setController(this);
        return loader;
    }

    public String getText(){
        return text.getText();
    }

    public void clearText(){
        text.clear();
    }

    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        controller.sendMessage();
    }
}
