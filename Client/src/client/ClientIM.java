package client;

import client.message.Message;
import client.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public class ClientIM implements Client {
    private Socket server;

    private InetAddress address;

    private int port;

    private BufferedReader serverInput;

    private PrintWriter serverOutput;

    public ClientIM(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean connect() {
        try {
            server = new Socket(address, port);
            serverInput = new BufferedReader(new InputStreamReader(server.getInputStream()));
            serverOutput = new PrintWriter(server.getOutputStream());
        } catch (IOException e) {
            return false;
        }
        System.out.println("Connected");

        return true;
    }

    @Override
    public void listenServer() {

    }

    private String  listenCommand(){
        return null;
    }

    @Override
    public int authenticate(AuthenticationData authenticationData) {
        return -1;
    }

    @Override
    public boolean register(User user, AuthenticationData authenticationData) {
        return false;
    }

    @Override
    public boolean sendMessage(Message message) {
        serverOutput.println(message.getAutorId() + ": " +message.getText());
        serverOutput.flush();
        System.out.println(message.getAutorId() + ": " +message.getText());
        return true;
    }

    @Override
    public boolean createDialog(List<User> userList) {
        return false;
    }
}
