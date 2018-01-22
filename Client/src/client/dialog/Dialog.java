package client.dialog;

import client.message.Message;

import java.time.ZonedDateTime;
import java.util.List;

public interface Dialog {
    void addMessage(Message message);

    List<Message> getMessages();

    List<Message> getMessagesAfter(ZonedDateTime dateTime);
}
