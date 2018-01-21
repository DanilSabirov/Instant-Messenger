package database;

import database.dialog.Dialog;
import database.user.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class XMLDatabase implements Database {
    private Path pathToRoot;

    public XMLDatabase(String pathToRoot) {
        this.pathToRoot = Paths.get(pathToRoot);
    }

    private void init(){
        if (Files.exists(pathToRoot) && Files.isDirectory(pathToRoot)) {

        }
    }

    @Override
    public boolean addUser(User user) {
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
