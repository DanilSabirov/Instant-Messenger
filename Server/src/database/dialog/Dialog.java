package database.dialog;

import database.message.Message;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface Dialog {
    int getId();

    Set<Integer> getUsersId();

    void addMessage(Message message);

    void addUser(int userId);

    List<Message> getMessages();

    List<Message> getMessagesAfter(ZonedDateTime dateTime);
}
