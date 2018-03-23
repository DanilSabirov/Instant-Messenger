package client.gui.view;

import client.Client;
import client.dialog.Dialog;
import client.gui.MainController;
import client.message.Message;
import client.user.User;
import client.user.UserIM;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class MainWindow {
    @FXML
    ListView dialogs;

    ListView foundUsers;

    @FXML
    VBox leftPanel;

    @FXML
    VBox rightPanel;

    @FXML
    ListView messages;

    @FXML
    TextArea text;

    @FXML
    Button send;

    @FXML
    Button searchButton;

    @FXML
    Button back;

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

    private int indexCurDialog = -1;

    public MainWindow(Client model, MainController controller) {
        this.model = model;
        this.controller = controller;
        dialogsObservableList = model.getDialogs();
        foundUsersObservableList = model.getFoundUsers();
        foundUsers = new ListView();
    }

    public FXMLLoader load(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/mainWindow.fxml"));
        loader.setController(this);
        return loader;
    }

    public void init() {
        dialogs.setItems((ObservableList) dialogsObservableList);
        dialogs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dialogs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                indexCurDialog = dialogsObservableList.indexOf(newValue);
                ListView messagesListView = new ListView(dialogsObservableList.get(indexCurDialog).getMessages());
                rightPanel.getChildren().set(1, messagesListView);
                setDisableMessagesWindow(false);
            }
        });

        foundUsers.setItems((ObservableList) foundUsersObservableList);

        foundUsers.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        foundUsers.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        foundUsers.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);

        foundUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        foundUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(newValue != null) {
                            controller.createDialog(((User) newValue).getId());
                        }
                    }
                });
            }
        });
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

    public void  clearSearch() {
        searchField.clear();
    }

    public void setUserInfo() {
        User user = model.getUser();

        name.setText(user.getName());
        id.setText(Integer.toString(user.getId()));
        email.setText(user.getEmail());
    }

    public void setDialogList() {
        leftPanel.getChildren().set(1, dialogs);
    }

    public void setFoundUsersList() {
        leftPanel.getChildren().set(1, foundUsers);
    }

    public void clearFoundUserList() {
        foundUsersObservableList.clear();
    }

    public void setDisableBackButton(boolean value) {
        back.setDisable(value);
    }

    public void setDisableMessagesWindow(boolean value) {
        messages.setDisable(value);
        send.setDisable(value);
        text.setDisable(value);
    }

    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        controller.sendMessage();
    }

    @FXML
    private void searchUser() {
        controller.searchUser();
    }

    @FXML
    private void onBack() {controller.back();}

    public int getIndexCurDialog() {
        return indexCurDialog;
    }
}
