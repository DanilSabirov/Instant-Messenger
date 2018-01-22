import database.AuthenticationData;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener implements Runnable {
    private static Logger log = Logger.getLogger(Listener.class.getName());

    private Socket client;

    private Server server;

    private BufferedReader clientInput = null;

    private PrintWriter clientOutput = null;

    public Listener(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientOutput = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        }
        try {
            int userId;
            do {
              //  System.out.println("Authentication");
                clientOutput.println("Login Password");
                clientOutput.flush();
                AuthenticationData authenticationData = new AuthenticationData(clientInput.readLine(), clientInput.readLine().toCharArray());
                userId = server.authenticate(authenticationData);
            }while (userId == -1);

            clientOutput.println(userId);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        }
    }

    private void listen() throws IOException {
        String line = "1";
        while (line != null){
            line = clientInput.readLine();
            System.out.println(line);
        }
    }
}
