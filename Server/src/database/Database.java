package database;

import database.dialog.Dialog;
import database.user.User;

public interface Database {
    boolean addUser(User user, AuthenticationData authenticationData);

    boolean removeUser(String name);

    User getUser(String name);

    boolean addDialog(Dialog dialog);

    boolean removeDialog(int id);

    void  mergeDialog(Dialog dialog);

    Dialog getDialog(int id);

    int searchAuthenticationData(AuthenticationData authenticationData);
}
