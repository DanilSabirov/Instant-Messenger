package client.dialog;

import client.message.Message;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface Dialog {
    int getId();

    void addMessage(Message message);

    List<Message> getMessages();

    List<Message> getMessagesAfter(ZonedDateTime dateTime);
}
