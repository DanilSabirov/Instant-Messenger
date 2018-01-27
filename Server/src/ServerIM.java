import database.AuthenticationData;
import database.Database;
import database.dialog.Dialog;
import database.user.User;
import database.user.UserIM;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerIM implements Server {
    private static Logger log = Logger.getLogger(ServerIM.class.getName());

    private Database database;

    private int port;

    private ServerSocket serverSocket;

    public ServerIM(Database database, int port) throws IOException {
        this.database = database;
        this.port = port;
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
}
