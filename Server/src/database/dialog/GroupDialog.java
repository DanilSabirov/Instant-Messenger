package database.dialog;

import database.message.Message;
import database.user.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class GroupDialog implements Dialog {
    private int id;

    private List<Message> messages;

    private Set<Integer> users;

    public GroupDialog() {
        messages = new ArrayList<>();
        users = new TreeSet<>();
    }

    public GroupDialog(int id, Set<Integer> usersId) {
        this.id = id;
        this.users = usersId;
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
    public Set<Integer> getUsersId() {
        return users;
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void addUser(int userId) {
        users.add(userId);
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
    public String toString() {
        return "GroupDialog{" +
                "id=" + id +
                ", messages=" + messages +
                ", users=" + users +
                '}';
    }
}
