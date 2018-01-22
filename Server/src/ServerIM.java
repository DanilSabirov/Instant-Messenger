import database.AuthenticationData;
import database.Database;
import database.dialog.Dialog;
import database.user.User;

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
                log.info("New connection");
            } catch (IOException e) {
                log.log(Level.SEVERE, "Exception: ", e);
                return;
            }
        }
        return;
    }

    @Override
    public Dialog getDialog(int idDialog) {
        return database.getDialog(idDialog);
    }

    @Override
    public int authenticate(AuthenticationData authenticationData) {
        int res = database.searchAuthenticationData(authenticationData);
        if (res != -1){
            return res;
        }
        return -1;
    }

    @Override
    public boolean register(User user, AuthenticationData authenticationData) {
        if (database.addUser(user, authenticationData)){
            return true;
        }
        return false;
    }
}
