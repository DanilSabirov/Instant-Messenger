package database;

import database.dialog.Dialog;
import database.user.User;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLDatabase implements Database {
    private Path pathToRoot;

    private static Logger log = Logger.getLogger(XMLDatabase.class.getName());

    public XMLDatabase(String pathToRoot) {
        this.pathToRoot = Paths.get(pathToRoot);
    }

    private void init(){
        if (Files.exists(pathToRoot) && Files.isDirectory(pathToRoot)) {

        }
    }

    @Override
    public boolean addUser(User user, AuthenticationData authenticationData) {
 /*
        try {

        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return false;
        }
        catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return false;
        }
        System.out.println("Done");*/
        return false;
    }

    @Override
    public boolean removeUser(String name) {
        return false;
    }

    @Override
    public User getUser(String name) {
        return null;
    }

    @Override
    public boolean addDialog(Dialog dialog) {
        return false;
    }

    @Override
    public boolean removeDialog(int id) {
        return false;
    }

    @Override
    public void mergeDialog(Dialog dialog) {

    }

    @Override
    public Dialog getDialog(int id) {
        return null;
    }

    @Override
    public int searchAuthenticationData(AuthenticationData authenticationData) {
        return 0;
    }
}
