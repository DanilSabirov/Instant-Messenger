import database.AuthenticationData;
import database.Database;
import database.dialog.Dialog;
import database.dialog.GroupDialog;
import database.message.Message;
import database.user.User;
import database.user.UserIM;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerIM implements Server {
    private static Logger log = Logger.getLogger(ServerIM.class.getName());

    private Database database;

    private ServerSocket serverSocket;

    private List<Observable> observables = new ArrayList<>();

    public ServerIM(Database database, int port) throws IOException {
        this.database = database;
        serverSocket = new ServerSocket(port);
    }

    public void start(){
        log.info("Server started");
        ExecutorService executor = Executors.newCachedThreadPool();
        while (Thread.currentThread().getState() != Thread.State.TERMINATED){
            try {
                Socket client = serverSocket.accept();
                executor.submit(new Listener(client, this));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Exception: ", e);
                return;
            }
        }
        return;
    }

    public void registerObservable(Observable client) {
        observables.add(client);
        log.info("Register observable: observable size = " + observables.size());
    }

    public void removeObservable(Observable client) {
        observables.remove(client);
        log.info("Remove observable: observable size = " + observables.size());
    }

    @Override
    public User getUser(int userId) {
        return database.getUser(userId);
    }

    private void notifyOfDialog(Dialog dialog) {
        log.info("Notify of dialog");
        for (Observable client: observables) {
            if (client.getUser() != null) {
                for (int userId: dialog.getUsersId()) {
                    if (client.getUser().getId() == userId) {
                        client.notifyOfDialog(dialog);
                        break;
                    }
                }
            }
        }
    }

    private void notifyOfMessage(Message message) {
        log.info("Notify of message");
        for (Observable client: observables) {
            if (client.getUser() != null) {
                for (int clientDialog: client.getUser().getDialogs()) {
                    if (clientDialog == message.getDialogId()) {
                        client.notifyOfMessage(message);
                    }
                }
            }
        }
    }

    @Override
    public int createDialog(Set<Integer> usersId) {
        log.info("Creating dialog");
        int dialogId = database.getSequenceDialogId();
        try {
            database.createDialog(new GroupDialog(dialogId, usersId));
            database.saveAll();
            notifyOfDialog(database.getDialog(dialogId));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (SAXException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
        return dialogId;
    }

    @Override
    public void addMessage(Message message) {
        try {
            database.addMessage(message);
            database.saveAll();
            notifyOfMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Dialog getDialog(int idDialog) {
        try {
            return database.getDialog(idDialog);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (SAXException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
        Runtime.getRuntime().exit(-1);
        return null;
    }

    @Override
    public synchronized User authenticate(AuthenticationData authenticationData) {
        int res = database.searchAuthenticationData(authenticationData);
        if (res != -1){
            return database.getUser(res);
        }
        return new UserIM(-1, null, null);
    }

    @Override
    public synchronized boolean register(User user, AuthenticationData authenticationData) {
        user.setId(database.getSequenceUserId());
        if (database.addUser(user, authenticationData)){
            try {
                database.saveAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public List<User> searchUsers(String namePrefix) {
        return database.searchUsers(namePrefix);
    }
}
