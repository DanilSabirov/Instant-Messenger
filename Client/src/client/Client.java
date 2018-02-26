package client;

import client.dialog.Dialog;
import client.message.Message;
import client.user.User;
import javafx.collections.ObservableList;

import javax.xml.stream.XMLStreamException;
import java.util.List;

public interface Client {
    boolean connect();

    void listenServer() throws XMLStreamException;

    void authenticate(AuthenticationData authenticationData) throws XMLStreamException;

    boolean register(User user, AuthenticationData authenticationData);

    void searchUser(String namePrefix);

    boolean sendMessage(Message message) throws XMLStreamException;

    void createDialog(int dialogId);

    ObservableList<Dialog> getDialogs();

    ObservableList<User> getFoundUsers();

    User getUser();

    void closeConnection();
}
