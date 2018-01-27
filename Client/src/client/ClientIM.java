package client;

import client.message.Message;
import client.user.User;
import client.user.UserIM;
import javafx.application.Platform;

import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientIM implements Client {
    private static Logger log = Logger.getLogger(ClientIM.class.getName());

    private Socket server;

    private InetAddress address;

    private int port;

    private InputStream serverInput;

    private OutputStream serverOutput;

    private XMLInputFactory inputFactory;

    private XMLStreamReader parser;

    private XMLOutputFactory outputFactory;

    private XMLEventFactory eventFactory;

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

            outputFactory = XMLOutputFactory.newInstance();
            eventFactory = XMLEventFactory.newFactory();

            inputFactory = XMLInputFactory.newInstance();
            parser = inputFactory.createXMLStreamReader(serverInput);
            writer = outputFactory.createXMLEventWriter(serverOutput);

            log.info("Connected");
            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent event = eventFactory.createStartElement("", null, "connection");
            writer.add(event);
            writer.add(end);
            writer.flush();

            listenServer();

            event = eventFactory.createEndElement("", null, "connection");
            writer.add(event);
            writer.add(end);
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return false;
        }

        return true;
    }

    @Override
    public void listenServer() {
        try {
            while (parser.hasNext()){
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT){
                    switch (parser.getLocalName()){
                        case "authentication":
                            Platform.runLater(() -> Main.setAuthorizationScene());
                            break;
                        case "authenticationResponse":
                            listenAuthenticationResponse();
                            break;
                    }
                }
                else if (event == XMLStreamConstants.END_ELEMENT){
                    if (parser.getLocalName().equals("connection")){
                        //connection closed
                        log.info("Connection closed");
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
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

    private boolean sendRegistrationRequest(User user, AuthenticationData authenticationData) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        int deep = 1;
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "registration");
        writer.add(event);
        writer.add(end);

        sendAuthenticateRequest(authenticationData);

        event = eventFactory.createStartElement("", null, "user");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        createNode("id", Integer.toString(user.getId()), deep+1);
        createNode("name", user.getName(), deep+1);
        createNode("email", user.getEmail(), deep+1);
        event = eventFactory.createEndElement("", null, "user");
        writer.add(tab);
        writer.add(event);
        writer.add(end);

        event = eventFactory.createEndElement("", null, "registration");
        writer.add(event);
        writer.add(end);
        writer.flush();

        return true;
    }

    private void sendAuthenticateRequest(AuthenticationData authenticationData) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        int deep = 1;
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "authenticationData");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        createNode("login", authenticationData.getLogin(), deep+1);
        createNode("password", new String(authenticationData.getPassword()), deep+1);
        event = eventFactory.createEndElement("", null, "authenticationData");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        writer.flush();
    }

    @Override
    public void authenticate(AuthenticationData authenticationData) {
        try {
            sendAuthenticateRequest(authenticationData);
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    private boolean listenAuthenticationResponse() throws XMLStreamException {
        while (parser.hasNext()) {
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (parser.getLocalName()) {
                    case "user":
                        user = listenUserData();
                        Platform.runLater(() -> Main.setMainScene());
                        return true;
                    case "error":
                        return false;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("authenticationResponse")){
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean register(User user, AuthenticationData authenticationData) {
        try {
            return sendRegistrationRequest(user, authenticationData);
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return false;
        }
    }

    @Override
    public boolean sendMessage(Message message) {

        return true;
    }

    @Override
    public boolean createDialog(List<User> userList) {
        return false;
    }

    public User getUser(){
        return user;
    }

    private void createNode(String name, String value, int deep) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));

        StartElement sElement = eventFactory.createStartElement("", "", name);
        writer.add(tab);
        writer.add(sElement);

        Characters characters = eventFactory.createCharacters(value);
        writer.add(characters);

        EndElement eElement = eventFactory.createEndElement("", "", name);
        writer.add(eElement);
        writer.add(end);
    }

    private static String lPad(int deep){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < deep; i++){
            s.append('\t');
        }
        return s.toString();
    }
}
