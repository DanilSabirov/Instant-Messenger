package client.gui.view;

import client.Client;
import client.dialog.Dialog;
import client.gui.MainController;
import client.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;


public class MainWindow {
    @FXML
    ListView dialogs;

    @FXML
    ListView messages;

    @FXML
    TextArea text;

    @FXML
    Button send;

    @FXML
    Button searchButton;

    @FXML
    TextField searchField;

    @FXML
    Label name;

    @FXML
    Label id;

    @FXML
    Label email;

    private Client model;

    private MainController controller;

    private ObservableList<Dialog> dialogsObservableList;

    private ObservableList<User> foundUsersObservableList;

    public MainWindow(Client model, MainController controller) {
        this.model = model;
        this.controller = controller;
        dialogsObservableList = model.getDialogs();
        foundUsersObservableList = model.getFoundUsers();
    }

    public FXMLLoader load(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/mainWindow.fxml"));
        loader.setController(this);
        return loader;
    }

    public String getText(){
        return text.getText();
    }

    public String getSearchUser() {
        return searchField.getText();
    }

    public void clearText(){
        text.clear();
    }

    public void setUserInfo() {
        User user = model.getUser();

        name.setText(user.getName());
        id.setText(Integer.toString(user.getId()));
        email.setText(user.getEmail());

        dialogs.setItems((ObservableList) foundUsersObservableList);
        dialogs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dialogs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                controller.createDialog(((User) newValue).getId());
            }
        });
        /*dialogsObservableList = FXCollections.observableList(user.getDialogs());
        dialogs.setItems((ObservableList) dialogsObservableList);
        dialogs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dialogs.getSelectionModel().selectionModeProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("createDialog");
                controller.createDialog((Integer) newValue);
            }
        });
*/
    }

    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        controller.sendMessage();
    }

    @FXML
    private void searchUser() {
        model.searchUser(searchField.getText());
    }
}
