package client.dialog;

import client.PairUserIdUserName;
import client.message.Message;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface Dialog {
    int getId();

    List<PairUserIdUserName> getPairsUserIdUserName();

    void addMessage(Message message);

    void addUser(int userId, String name);

    ObservableList<Message> getMessages();

    List<Message> getMessagesAfter(ZonedDateTime dateTime);
}