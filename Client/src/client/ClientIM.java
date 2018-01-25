package client;

import client.message.Message;
import client.user.User;
import client.user.UserIM;
import javafx.application.Platform;

import javax.xml.stream.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientIM implements Client {
    private Socket server;

    private InetAddress address;

    private int port;

    private InputStream serverInput;

    private OutputStream serverOutput;

    private XMLInputFactory inputFactory;

    private XMLStreamReader parser;

    private XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private XMLEventWriter writer;

    private User user;

    public ClientIM(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean connect() {
        try {
            server = new Socket(address, port);

            serverInput = server.getInputStream();
            serverOutput = server.getOutputStream();

            inputFactory = XMLInputFactory.newInstance();
            parser = inputFactory.createXMLStreamReader(serverInput);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            return false;
        }

        System.out.println("Connected");
        listenServer();
        return true;
    }

    @Override
    public void listenServer() {
        try {
            while (parser.hasNext()){
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT){
                    switch (parser.getLocalName()){
                        case "user":
                            user = listenUserData();
                            System.out.println(user);
                            break;
                        case "authentication":
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.setAuthorizationScene();
                                }
                            });
                            break;
                    }
                }
                else if (event == XMLStreamConstants.END_ELEMENT){
                    if (parser.getLocalName().equals("connection")){
                        //connection closed
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private User listenUserData() throws XMLStreamException {
        String name = null;
        String id = null;
        String email = null;

        while (parser.hasNext()){
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT){
                switch (parser.getLocalName()) {
                    case "id":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            id = parser.getText();
                        }
                        break;
                    case "name":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            name = parser.getText();
                        }
                        break;
                    case "email":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            email = parser.getText();
                        }
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("user")){
                    break;
                }
            }
        }

        return new UserIM(Integer.parseInt(id), name, email);
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

    }

    @Override
    public boolean sendMessage(Message message) {

        return true;
    }

    @Override
    public boolean createDialog(List<User> userList) {
        return false;
    }

    private User getUser(){
        return user;
    }
}
