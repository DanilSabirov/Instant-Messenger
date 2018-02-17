package client.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UserIM implements User {
    private int id;

    private String name;

    private String email;

    private List<Integer> dialogsId;

    public UserIM(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        dialogsId = new ArrayList<>();
    }

    public UserIM(int id, String name, String email, List<Integer> dialogsId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dialogsId = dialogsId;
    }

    public List<Integer> getDialogs() {
        return dialogsId;
    }

    public void addNewDialog(int dialogId) {
        dialogsId.add(dialogId);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + "\n" + name;
    }
}
