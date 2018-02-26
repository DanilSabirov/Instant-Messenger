package client.dialog;

import client.PairUserIdUserName;
import client.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.ZonedDateTime;
import java.util.*;

public class GroupDialog implements Dialog {
    private int id;

    private ObservableList<Message> messages;

    private List<PairUserIdUserName> pairsUserIdUserName;

    public GroupDialog() {
        messages = FXCollections.observableList(new ArrayList<>());
        pairsUserIdUserName = new ArrayList<>();
    }

    public GroupDialog(int id) {
        this.id = id;
        messages = FXCollections.observableList(new ArrayList<>());
        pairsUserIdUserName = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<PairUserIdUserName> getPairsUserIdUserName() {
        return pairsUserIdUserName;
    }


    @Override
    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void addUser(int userId, String name) {
        pairsUserIdUserName.add(new PairUserIdUserName(userId, name));
    }

    @Override
    public ObservableList<Message> getMessages() {
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
        StringBuilder str = new StringBuilder();
        str.append(id);
        str.append("\n");
        for (int i = 0; i < pairsUserIdUserName.size(); i++) {
            str.append(pairsUserIdUserName.get(i).getUserName());
            if (i != pairsUserIdUserName.size()-1) {
                str.append(" ,");
            }
        }
        return str.toString();
    }
}
