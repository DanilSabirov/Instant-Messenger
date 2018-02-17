import database.AuthenticationData;
import database.dialog.Dialog;
import database.message.Message;
import database.user.User;
import database.user.UserIM;

import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener implements Runnable {
    private static Logger log = Logger.getLogger(Listener.class.getName());

    private Socket client;

    private Server server;

    private InputStream clientInput;

    private OutputStream clientOutput;

    private XMLInputFactory inputFactory;

    private XMLStreamReader parser;

    private XMLOutputFactory outputFactory;

    private XMLEventFactory eventFactory;

    private XMLEventWriter writer;

    public Listener(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        log.info("New connection: " + Thread.currentThread().getId());
        try {
            clientInput = client.getInputStream();
            clientOutput = client.getOutputStream();

            outputFactory = XMLOutputFactory.newInstance();
            eventFactory = XMLEventFactory.newFactory();
            inputFactory = XMLInputFactory.newInstance();

            writer = outputFactory.createXMLEventWriter(clientOutput);
          //  writer = outputFactory.createXMLEventWriter(System.out);

            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent event = eventFactory.createStartElement("", null, "connection");
            writer.add(event);
            writer.add(end);
            writer.flush();

            parser = inputFactory.createXMLStreamReader(clientInput);
            sendAuthenticationRequest(1);
            listen();

            client.close();
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        }
    }

    private void listen() throws IOException {
        User user = null;
        try {
            while (parser.hasNext()){
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    switch (parser.getLocalName()) {
                        case "authenticationData":
                            log.info("Authentication");
                            user = server.authenticate(listenAuthenticationData());
                            sendAuthenticationResponse(user);
                            break;
                        case "registration":
                            log.info("Registration");
                            AuthenticationData data = listenAuthenticationData();
                            user = listenUserData();
                            server.register(user, data);
                            break;
                        default:
                            if (user != null) {
                                switch (parser.getLocalName()) {
                                    case "getDialog":
                                        event = parser.next();
                                        int idDialog = Integer.parseInt(parser.getText());
                                        if (user.getDialogs().contains(idDialog)) {
                                            server.getDialog(idDialog);
                                        }
                                    case "searchUser":
                                        event = parser.next();
                                        String namePrefix = parser.getText();
                                        sendFoundUsers(server.searchUsers(namePrefix));
                                    case "createDialog":
                                        event = parser.next();
                                        Integer userId = Integer.parseInt(parser.getText());

                                        Set<Integer> usersId = new TreeSet<>();
                                        usersId.add(user.getId());
                                        usersId.add(userId);
                                        int dialogId = server.createDialog(usersId);
                                        sendDialog(server.getDialog(dialogId));
                                }
                            }
                    }
                }
                else if (event == XMLStreamConstants.END_ELEMENT){
                    if (parser.getLocalName().equals("connection")){
                        log.info("connection " + Thread.currentThread().getId() + " closed");
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private AuthenticationData listenAuthenticationData() throws XMLStreamException {
        String login = null;
        char[] password = null;

        while (parser.hasNext()){
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT){
                switch (parser.getLocalName()) {
                    case "login":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            login = parser.getText();
                        }
                        break;
                    case "password":
                        event = parser.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            password = parser.getText().toCharArray();
                        }
                        break;
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT){
                if (parser.getLocalName().equals("authenticationData")){
                    break;
                }
            }
        }

        return new AuthenticationData(login, password);
    }

    private User listenUserData() throws XMLStreamException {
        String name = null;
        String email = null;

        while (parser.hasNext()){
            int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT){
                switch (parser.getLocalName()) {
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

        return new UserIM(-1, name, email);
    }

    private void sendAuthenticationRequest(int deep) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        writer.add(tab);
        createNode("authentication", "", deep-1);

        writer.flush();
    }

    private void sendAuthenticationResponse(User response) throws XMLStreamException {
        int deep = 1;
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "authenticationResponse");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        if (response.getId() != -1){
            log.info("Sending user: " + response.toString());
            sendUser(response, deep+1);
        }
        else {
            createNode("error", "incorrect", deep+1);
        }
        event = eventFactory.createEndElement("", null, "authenticationResponse");
        writer.add(tab);
        writer.add(event);
        writer.add(end);

        writer.flush();
    }

    private void sendRegistrationResponse(String response) throws XMLStreamException {
        createNode("registrationResponse", response, 1);
        writer.flush();
    }

    private void sendFoundUsers(List<User> users) throws XMLStreamException {
        int deep = 1;
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "foundUsers");
        writer.add(tab);
        writer.add(event);
        writer.add(end);

        for (int i = 0; i < users.size(); i++){
            sendUser(users.get(i), deep+1);
        }

        event = eventFactory.createEndElement("", null, "foundUsers");
        writer.add(tab);
        writer.add(event);
        writer.add(end);

        writer.flush();
    }

    private void sendUser(User user, int deep) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        writer.add(tab);
        event = eventFactory.createStartElement("", null, "user");
        writer.add(event);
        writer.add(end);

        createNode("id", Integer.toString(user.getId()), deep+1);
        createNode("name", user.getName(), deep+1);
        createNode("email", user.getEmail(), deep+1);

        event = eventFactory.createStartElement("", null,"dialogsId");
        writer.add(tab);
        writer.add(event);
        writer.add(end);
        for (Integer dialogId: user.getDialogs()) {
            createNode("dialogId", dialogId.toString(), deep+1);
        }
        event = eventFactory.createEndElement("", null, "dialogsId");
        writer.add(tab);
        writer.add(event);
        writer.add(end);

        writer.add(tab);
        event = eventFactory.createEndElement("", null, "user");
        writer.add(event);
        writer.add(end);

        writer.flush();
    }

    private void sendDialog(Dialog dialog) throws XMLStreamException {
        int deep = 1;
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        writer.add(tab);
        event = eventFactory.createStartElement("", null, "dialog");
        writer.add(event);
        writer.add(end);

        createNode("dialogId", Integer.toString(dialog.getId()), deep+1);
        writer.add(end);

        for (Message message: dialog.getMessages()) {
            sendMessage(message, deep+1);
        }

        writer.add(tab);
        event = eventFactory.createEndElement("", null, "dialog");
        writer.add(event);
        writer.add(end);

        writer.flush();
    }

    private void sendMessage(Message message, int deep) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        writer.add(tab);
        event = eventFactory.createStartElement("", null, "message");
        writer.add(event);
        writer.add(end);

        createNode("authorId", Integer.toString(message.getAuthorId()), deep+1);
        createNode("text", message.getText(), deep);
        createNode("time", message.getDateReceipt().toString(), deep+1);

        writer.add(tab);
        event = eventFactory.createEndElement("", null, "message");
        writer.add(event);
        writer.add(end);
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
        StringBuffer s = new StringBuffer();
        for(int i = 0; i < deep; i++){
            s.append('\t');
        }
        return s.toString();
    }
}
