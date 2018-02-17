package client.dialog;

import client.message.Message;
import client.user.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class GroupDialog implements Dialog {
    private int id;

    private List<Message> messages;

    public GroupDialog() {
        messages = new ArrayList<>();
    }

    public GroupDialog(int id) {
        this.id = id;
        messages = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<Message> getMessagesAfter(ZonedDateTime dateTime) {
        List<Message> res = new ArrayList<>();
        int lastPos = messages.size() - 1;
        for(int i = lastPos; i >= 0; i--) {
            Message m = messages.get(i);
            if (m.getDateReceipt().compareTo(dateTime) == 1){
                res.add(m);
            }
            else {
                break;
            }
        }
        Collections.reverse(res);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupDialog that = (GroupDialog) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "GroupDialog{" +
                "id=" + id +
                ", messages=" + messages +
                '}';
    }
}
