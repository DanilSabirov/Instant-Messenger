package database;

import database.dialog.Dialog;
import database.loader.LoaderXML;
import database.user.User;
import database.user.UserIM;
import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLDatabase implements Database {
    private static Logger log = Logger.getLogger(XMLDatabase.class.getName());

    private Path pathToRoot;

    private LoaderXML loaderXML;

    private DocumentBuilderFactory factory;

    private DocumentBuilder documentBuilder;

    private Document usersDoc;

    private Document authenticationDataDoc;

    private File usersXML;

    private File authenticationDataXML;

    public XMLDatabase(String pathToRoot) {
        this.pathToRoot = Paths.get(pathToRoot);
    }

    int sequenceUserId = 1;

    @Override
    public void init() {
        try {
            loaderXML = new LoaderXML();
            factory = DocumentBuilderFactory.newInstance();
            documentBuilder = factory.newDocumentBuilder();

            usersXML = new File("users.xml");
            authenticationDataXML = new File("authenticationData.xml");

            usersDoc = loaderXML.load(pathToRoot, usersXML, "user");
            authenticationDataDoc = loaderXML.load(pathToRoot, authenticationDataXML, "authenticationData");

        } catch (ParserConfigurationException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (SAXException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    @Override
    public boolean addUser(User user, AuthenticationData authenticationData) {
        Element userElement = usersDoc.createElement("user");

        Element nameElement = usersDoc.createElement("name");
        nameElement.appendChild(usersDoc.createTextNode(user.getName()));

        Element idElement = usersDoc.createElement("id");
        idElement.appendChild(usersDoc.createTextNode(Integer.toString(user.getId())));

        Element emailElement = usersDoc.createElement("email");
        emailElement.appendChild(usersDoc.createTextNode(user.getEmail()));

        usersDoc.getDocumentElement().appendChild(userElement);
        userElement.appendChild(idElement);
        userElement.appendChild(nameElement);
        userElement.appendChild(emailElement);

        Element authenticationElement = authenticationDataDoc.createElement("data");

        Element loginElement = authenticationDataDoc.createElement("login");
        loginElement.appendChild(authenticationDataDoc.createTextNode(authenticationData.getLogin()));

        Element passwordElement = authenticationDataDoc.createElement("password");
        passwordElement.appendChild(authenticationDataDoc.createTextNode(new String(authenticationData.getPassword())));

        Element idUserElement = authenticationDataDoc.createElement("id");
        idUserElement.appendChild(authenticationDataDoc.createTextNode(Integer.toString(user.getId())));

        authenticationDataDoc.getDocumentElement().appendChild(authenticationElement);
        authenticationElement.appendChild(loginElement);
        authenticationElement.appendChild(passwordElement);
        authenticationElement.appendChild(idUserElement);

        usersDoc.getDocumentElement().appendChild(userElement);
        userElement.appendChild(idElement);
        userElement.appendChild(nameElement);
        userElement.appendChild(emailElement);

        return true;
    }

    @Override
    public boolean removeUser(String name) {
        return false;
    }

    @Override
    public User getUser(String name) {
        return null;
    }

    @Override
    public User getUser(int id) {
        return searchUser(id);
    }

    @Override
    public boolean addDialog(Dialog dialog) {
        return false;
    }

    @Override
    public boolean removeDialog(int id) {
        return false;
    }

    @Override
    public void mergeDialog(Dialog dialog) {

    }

    @Override
    public Dialog getDialog(int id) {
        return null;
    }

    @Override
    public int searchAuthenticationData(AuthenticationData authenticationData) {
        log.info("Search: " + authenticationData.getLogin());
        NodeList listData = authenticationDataDoc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < listData.getLength(); i++){
            if (listData.item(i).getNodeName().equals("data")) {
                NodeList values = listData.item(i).getChildNodes();
                String login = null;
                char[] password = null;
                int id = -1;
                for(int j = 0; j < values.getLength(); j++){
                    Node value = values.item(j);
                    if (value.getNodeName().equals("login")){
                        login = value.getTextContent();
                    }
                    else if(value.getNodeName().equals("password")){
                        password = value.getTextContent().toCharArray();
                    }
                    else if(value.getNodeName().equals("id")){
                        id = Integer.parseInt(value.getTextContent());
                    }
                }

                AuthenticationData data = new AuthenticationData(login, password);
                if (authenticationData.equals(data)){
                    log.info("Found: " + id);
                    return id;
                }
            }
        }
        log.info("Not found!");
        return -1;
    }

    @Override
    public int getSequenceUserId() {
        return sequenceUserId;
    }

    public User searchUser(int userId) {
        log.info("Search: " + userId);
        NodeList listData = usersDoc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < listData.getLength(); i++){
            if (listData.item(i).getNodeName().equals("user")) {
                NodeList values = listData.item(i).getChildNodes();
                String name = null;
                int id = -1;
                String email = null;
                for(int j = 0; j < values.getLength(); j++){
                    Node value = values.item(j);
                    if (value.getNodeName().equals("name")){
                        name = value.getTextContent();
                    }
                    else if(value.getNodeName().equals("email")){
                        email = value.getTextContent();
                    }
                    else if(value.getNodeName().equals("id")){
                        id = Integer.parseInt(value.getTextContent());
                    }
                }

                if (userId == id){
                    log.info("Found: " + id);
                    return new UserIM(id, name, email);
                }
            }
        }
        log.info("Not found!");
        return new UserIM(-1, null, null
        );
    }

    public void save(Document document, File file) throws IOException {
        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer ser = implLS.createLSSerializer();
        ser.getDomConfig().setParameter("format-pretty-print", true);

        LSOutput out = implLS.createLSOutput();
        out.setEncoding("UTF-8");
        out.setByteStream(Files.newOutputStream(new File(pathToRoot.toFile(), file.toString()).toPath()));
        ser.write(document, out);
    }

    @Override
    public void saveAll() throws IOException {
        save(usersDoc, usersXML);
        save(authenticationDataDoc, authenticationDataXML);
    }

}
