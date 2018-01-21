import database.AuthenticationData;
import database.Database;
import database.dialog.Dialog;
import database.user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerIM implements Server {
    private Database database;

    private int port;

    private ServerSocket serverSocket;

    public ServerIM(Database database, int port) throws IOException {
        this.database = database;
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    public void start(){
        System.out.println("Server started");
        ExecutorService executor = Executors.newCachedThreadPool();
        while (Thread.currentThread().getState() != Thread.State.TERMINATED){
            try {
                Socket client = serverSocket.accept();
                executor.submit(new Listener(client, this));
                System.out.println("New connection");
            } catch (IOException e) {
                e.printStackTrace();
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
    public boolean register(User user) {
        if (database.addUser(user)){
            return true;
        }
        return false;
    }
}
