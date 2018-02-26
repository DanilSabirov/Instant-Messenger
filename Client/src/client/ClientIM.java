package client;

import client.dialog.Dialog;
import client.dialog.GroupDialog;
import client.message.Message;
import client.message.UserMessage;
import client.user.User;
import client.user.UserIM;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
import java.time.ZonedDateTime;
import java.util.*;
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

    private ObservableList<Dialog> dialogs;

    private ObservableList<User> foundUsers;

    public ClientIM(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        dialogs = FXCollections.observableList(new ArrayList<>());
        foundUsers = FXCollections.observableList(new ArrayList<>());
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

            server.close();
            log.info("Connection closed");
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
                            if (listenAuthenticationResponse()) {
                                for (Integer i: user.getDialogs()) {
                                    sendGetDialogRequest(i);
                                }
                            }
                            break;
                        case "dialog":
                            Dialog dialog = listenDialog();
                            if (!dialogs.contains(dialog)) {
                                Platform.runLater(() -> dialogs.add(dialog));
                            }
                            break;
                        case "foundUsers":
                            List<User> userList= listenFoundUsers();
                            Platform.runLater(() -> foundUsers.addAll(userList));
                            break;
                        case "message":
                            Message message = listenMessage();
                            Platform.runLater(() -> dialogs.get(foundDialogIndex(message.getDialogId())).addMessage(message));
                            break;
                    }
                }
                else if (event == XMLStreamConstants.END_ELEMENT){
                    if (parser.getLocalName().equals("connection")) {
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    @Override
    public void authenticate(AuthenticationData authenticationData) {
        try {
            sendAuthenticateRequest(authenticationData);
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
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
    public void searchUser(String namePrefix) {
        try {
            sendSearchUserRequest(namePrefix);
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    @Override
    public boolean sendMessage(Message message) throws XMLStreamException {
        log.info("Sending message: " + message);

        XMLEvent end = eventFactory.createDTD("\n");
        int deep = 1;
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "message");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        createNode("authorId", Integer.toString(message.getAuthorId()), deep+1);
        createNode("dialogId", Integer.toString(message.getDialogId()), deep+1);
        createNode("text", message.getText(), deep+1);
        event = eventFactory.createEndElement("", null, "message");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        writer.flush();
        return true;
    }

    @Override
    public void createDialog(int userId) {
        try {
            sendCreateDialogRequest(userId);
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    @Override
    public ObservableList<Dialog> getDialogs() {
        return dialogs;
    }

    @Override
    public User getUser(){
        return user;
    }

    @Override
    public ObservableList<User> getFoundUsers() {
        return foundUsers;
    }

    @Override
    public void closeConnection() {
        try {
            XMLEvent event;
            event = eventFactory.createEndElement("", null,"connection");
            writer.add(event);

            writer.flush();
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    public void getDialog(int dialogId){
        if (!dialogs.contains(new GroupDialog(dialogId))) {
            try {
                sendGetDialogRequest(dialogId);
            } catch (XMLStreamException e) {
                log.log(Level.SEVERE, "Exception: ", e);
            }
        }
    }

    private User listenUserData() throws XMLStreamException {
        String name = null;
        String id = null;
        String email = null;
        List<Integer> dialogsId = new ArrayList<>();

        while (parser.hasNext()) {
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
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
                    case "dialogsId":
                        dialogsId = listenDialogsId();
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("user")){
                    break;
                }
            }
        }

        return new UserIM(Integer.parseInt(id), name, email, dialogsId);
    }

    private List listenFoundUsers() throws XMLStreamException {
        List users = new ArrayList();

        while (parser.hasNext()) {
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (parser.getLocalName()) {
                    case "user":
                        users.add(listenUserData());
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("foundUsers")){
                    break;
                }
            }
        }

        return users;
    }

    private List<Integer> listenDialogsId() throws XMLStreamException {
        ArrayList<Integer> idSet = new ArrayList<>();

        while (parser.hasNext()){
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT){
                switch (parser.getLocalName()) {
                    case "dialogId":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            idSet.add(Integer.parseInt(parser.getText()));
                        }
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("dialogsId")){
                    break;
                }
            }
        }

        return idSet;
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

    private Dialog listenDialog() throws XMLStreamException {
        log.info("Listening dialog");
        GroupDialog dialog = new GroupDialog();

        List<Integer> usersId = null;
        List<String> userName = null;

        while (parser.hasNext()) {
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (parser.getLocalName()) {
                    case "dialogId":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            dialog.setId(Integer.parseInt(parser.getText()));
                        }
                        break;
                    case "usersId":
                        usersId = listenUsersId();
                        break;
                    case "usersName":
                        userName = listenUsersName();
                        break;
                    case "message":
                        dialog.addMessage(listenMessage());
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("dialog")){
                    break;
                }
            }
        }

        for (int i = 0; i < usersId.size(); i++) {
            dialog.addUser(usersId.get(i), userName.get(i));
        }

        return dialog;
    }

    private List<String> listenUsersName() throws XMLStreamException {
        List<String> users = new ArrayList<>();

        while (parser.hasNext()) {
            int event = parser.next();
            if(event == XMLStreamConstants.START_ELEMENT) {
                switch (parser.getLocalName()) {
                    case "userName":
                        event = parser.next();
                        users.add(parser.getText());
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("usersName")){
                    break;
                }
            }
        }

        return users;
    }

    private List<Integer> listenUsersId() throws XMLStreamException {
        List<Integer> users = new ArrayList<>();

        while (parser.hasNext()) {
            int event = parser.next();
            if(event == XMLStreamConstants.START_ELEMENT) {
                switch (parser.getLocalName()) {
                    case "userId":
                        event = parser.next();
                        users.add(Integer.parseInt(parser.getText()));
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("usersId")){
                    break;
                }
            }
        }

        return users;
    }

    private Message listenMessage() throws XMLStreamException {
        log.info("Listening message");

        int authorId = -1;
        String authorName = null;
        int dialogId = -1;
        String text = "";
        ZonedDateTime time = null;

        while (parser.hasNext()){
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT){
                switch (parser.getLocalName()) {
                    case "authorId":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            authorId = Integer.parseInt(parser.getText());
                        }
                        break;
                    case "authorName":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            authorName = parser.getText();
                        }
                        break;
                    case "dialogId":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            dialogId = Integer.parseInt(parser.getText());
                        }
                        break;
                    case "text":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            text = parser.getText();
                        }
                        break;
                    case "time":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
             //               time = ZonedDateTime.parse(parser.getText());
                            time = ZonedDateTime.now();
                        }
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("message")){
                    break;
                }
            }
        }

        return new UserMessage(authorId, authorName, dialogId, text, time);
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

    private void sendGetDialogRequest(int dialogId) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        int deep = 1;

        createNode("getDialog", Integer.toString(dialogId), 1);
        writer.add(end);

        writer.flush();
    }

    private void sendCreateDialogRequest(int userId) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        int deep = 1;

        createNode("createDialog", Integer.toString(userId), deep);
        writer.add(end);

        writer.flush();
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

    private void sendSearchUserRequest(String namePrefix) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        int deep = 1;

        createNode("searchUser", namePrefix, deep);
        writer.add(end);

        writer.flush();
    }     ZonedDateTime z = ZonedDateTime.now();
        ZonedDateTime t = ZonedDateTime.parse(z.toString());

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

        writer.flush();
    }

    private int foundDialogIndex(int dialogId) {
        for (int i = 0; i < dialogs.size(); i++) {
            if (dialogs.get(i).getId() == dialogId) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    private static String lPad(int deep){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < deep; i++){
            s.append('\t');
        }
        return s.toString();
    }
}
