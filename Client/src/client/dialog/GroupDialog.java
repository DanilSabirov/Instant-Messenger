package client.dialog;

import client.message.Message;
import client.user.User;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupDialog implements Dialog {
    private int id;

    private List<Message> messages;

    private List<User> users;

    public GroupDialog(int id, List<User> users) {
        this.id = id;
        this.users = users;
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
}
