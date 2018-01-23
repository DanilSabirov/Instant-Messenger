import database.AuthenticationData;
import database.user.User;
import database.user.UserIM;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener implements Runnable {
    private static Logger log = Logger.getLogger(Listener.class.getName());

    private Socket client;

    private Server server;

    private InputStream clientInput = null;

    private OutputStream clientOutput = null;

    private XMLOutputFactory factory = XMLOutputFactory.newInstance();

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
            writer = factory.createXMLEventWriter(clientOutput);

            sendUser(new UserIM(1, "Bob", "gmail.com"));
        } catch (XMLStreamException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
            return;
        }
    }

    private void listen() throws IOException {
        String line = "1";
        while (line != null){

            System.out.println(line);
        }
    }

    private void sendAuthenticationRequest() throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "authentication");
        writer.add(event);
        writer.add(end);
        event = eventFactory.createEndElement("", null, "authentication");
        writer.add(event);
        writer.add(end);

        writer.flush();
    }

    private void sendUser(User user) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent event;

        event = eventFactory.createStartElement("", null, "user");
        writer.add(event);
        writer.add(end);
        createNode("id", Integer.toString(user.getId()));
        createNode("name", user.getName());
        createNode("email", user.getEmail());
        event = eventFactory.createEndElement("", null, "user");
        writer.add(event);
        writer.add(end);

        writer.flush();
    }

    private void createNode(String name, String value) throws XMLStreamException {
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        StartElement sElement = eventFactory.createStartElement("", "", name);
        writer.add(tab);
        writer.add(sElement);

        Characters characters = eventFactory.createCharacters(value);
        writer.add(characters);

        EndElement eElement = eventFactory.createEndElement("", "", name);
        writer.add(eElement);
        writer.add(end);
    }
}
