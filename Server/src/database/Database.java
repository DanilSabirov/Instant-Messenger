package database;

import database.dialog.Dialog;
import database.message.Message;
import database.user.User;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public interface Database {
    public void init();

    boolean addUser(User user, AuthenticationData authenticationData);

    boolean removeUser(String name);

    User getUser(String name);

    User getUser(int id);

    void createDialog(Dialog dialog) throws IOException, SAXException;

    public void addNewUserToDialog(int dialogId, int userId) throws IOException, SAXException;

    void addMessage(Message message, int dialogId) throws IOException, SAXException;

    boolean removeDialog(int id);

    void  mergeDialog(Dialog dialog);

    Dialog getDialog(int id) throws IOException, SAXException;

    int searchAuthenticationData(AuthenticationData authenticationData);

    int getSequenceUserId();

    void saveAll() throws IOException;
}
