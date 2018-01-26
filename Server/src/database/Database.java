package database;

import database.dialog.Dialog;
import database.user.User;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public interface Database {
    public void init();

    boolean addUser(User user, AuthenticationData authenticationData);

    boolean removeUser(String name);

    User getUser(String name);

    User getUser(int id);

    boolean addDialog(Dialog dialog);

    boolean removeDialog(int id);

    void  mergeDialog(Dialog dialog);

    Dialog getDialog(int id);

    int searchAuthenticationData(AuthenticationData authenticationData);

    int getSequenceUserId();

    void saveAll() throws IOException;
}
