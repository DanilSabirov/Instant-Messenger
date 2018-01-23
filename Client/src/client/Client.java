package client;

import client.message.Message;
import client.user.User;

import javax.xml.stream.XMLStreamException;
import java.util.List;

public interface Client {
    boolean connect();

    void listenServer() throws XMLStreamException;

    int authenticate(AuthenticationData authenticationData);

    boolean register(User user, AuthenticationData authenticationData);

    boolean sendMessage(Message message);

    boolean createDialog(List<User> userList);
}
