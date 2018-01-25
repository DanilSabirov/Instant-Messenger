import database.AuthenticationData;
import database.user.User;

import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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

    private XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

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
            writer = outputFactory.createXMLEventWriter(clientOutput);
          //  writer = outputFactory.createXMLEventWriter(System.out);

            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent event = eventFactory.createStartElement("", null, "connection");
            writer.add(event);
            writer.add(end);
            writer.flush();

            sendAuthenticationRequest(1);
            listen();

            event = eventFactory.createEndElement("", null, "connection");
            writer.add(event);
            writer.add(end);

            writer.flush();

        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        }
    }

    private void listen() throws IOException {
        try {
            while (parser.hasNext()){
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT){
                    switch (parser.getLocalName()){
                        case "authenticationData":
                            AuthenticationData authenticationData = listenAuthenticationData();

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

    private void sendAuthenticationRequest(int deep) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD(lPad(deep));
        XMLEvent event;

        writer.add(tab);
        createNode("authentication", "", deep-1);

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
        writer.add(tab);
        event = eventFactory.createEndElement("", null, "user");
        writer.add(event);
        writer.add(end);

        writer.flush();
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
