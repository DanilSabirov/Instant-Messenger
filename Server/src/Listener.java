import database.AuthenticationData;

import java.io.*;
import java.net.Socket;

public class Listener implements Runnable {
    private Socket client;

    private Server server;

    public Listener(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        BufferedReader clientInput = null;
        PrintWriter clientOutput = null;
        try {
            clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientOutput = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            int userId;
            do {
                clientOutput.println("Login Password");
                AuthenticationData authenticationData = new AuthenticationData(clientInput.readLine(), clientInput.readLine().toCharArray());
                userId = server.authenticate(authenticationData);
            }while (userId == -1);

            clientOutput.println(userId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


    }
}
