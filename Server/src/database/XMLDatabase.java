package database;

import database.dialog.Dialog;
import database.dialog.GroupDialog;
import database.loader.LoaderXML;
import database.message.Message;
import database.message.UserMessage;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
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

    private TreeMap<Integer, Document> dialogDocs;

    private File usersXML;

    private File authenticationDataXML;

    public XMLDatabase(String pathToRoot) {
        this.pathToRoot = Paths.get(pathToRoot);
        dialogDocs = new TreeMap<>();
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
    public void createDialog(Dialog dialog) throws IOException, SAXException {
        Document dialogDoc = getDialogDoc(dialog.getId());

        initDialogDoc(dialog.getId(), dialogDoc);

        for (Integer userId: dialog.getUsersId()) {
            addNewUserToDialogDoc(dialogDoc, userId);
        }
    }

    @Override
    public void addNewUserToDialog(int dialogId, int userId) throws IOException, SAXException {
        if (!dialogDocs.containsKey(dialogId)) {
            dialogDocs.put(dialogId, getDialogDoc(dialogId));
        }

        Document dialogDoc = dialogDocs.get(dialogId);

        addNewUserToDialogDoc(dialogDoc, userId);
    }

    private void addNewUserToDialogDoc(Document dialogDoc, int userId) {
        NodeList nodeList = dialogDoc.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("users")){
                Element newUserId = dialogDoc.createElement("userId");
                newUserId.appendChild(dialogDoc.createTextNode(Integer.toString(userId)));

                node.appendChild(newUserId);
                break;
            }
        }
    }

    @Override
    public void addMessage(Message message, int dialogId) throws IOException, SAXException {
        if (!dialogDocs.containsKey(dialogId)) {
            dialogDocs.put(dialogId, getDialogDoc(dialogId));
        }

        Document dialogDoc = dialogDocs.get(dialogId);

        Element messageElement = dialogDoc.createElement("message");

        Element authorIdElement = dialogDoc.createElement("authorId");
        authorIdElement.appendChild(dialogDoc.createTextNode(Integer.toString(message.getAutorId())));

        Element textElement = dialogDoc.createElement("text");
        textElement.appendChild(dialogDoc.createTextNode(message.getText()));

        Element timeElement = dialogDoc.createElement("text");
        timeElement.appendChild(dialogDoc.createTextNode(message.getDateReceipt().toString()));

        messageElement.appendChild(authorIdElement);
        messageElement.appendChild(textElement);
        messageElement.appendChild(timeElement);

        dialogDoc.getDocumentElement().appendChild(messageElement);
    }

    @Override
    public boolean removeDialog(int id) {
        return false;
    }

    @Override
    public void mergeDialog(Dialog dialog) {

    }

    @Override
    public Dialog getDialog(int id) throws IOException, SAXException {
        return parseDialog(getDialogDoc(id));
    }

    private Document getDialogDoc(int id) throws IOException, SAXException {
        Document dialogDoc;
        if (dialogDocs.containsKey(id)){
            dialogDoc = dialogDocs.get(id);
        }
        else{
            File file = new File(Integer.toString(id) + ".xml");
            dialogDoc = loaderXML.load(pathToRoot, file, "dialog");
            dialogDocs.put(id, dialogDoc);
        }
        return dialogDoc;
    }

    private Dialog parseDialog(Document dialogDoc) {
        NodeList messageList = dialogDoc.getChildNodes().item(0).getChildNodes();
        GroupDialog dialog = new GroupDialog();

        for (int i = 0; i < messageList.getLength(); i++) {
            Node message = messageList.item(i);
            switch (message.getNodeName()) {
                case "id" :
                    dialog.setId(Integer.parseInt(message.getTextContent()));
                    break;
                case "usersId" :
                    NodeList usersId = message.getChildNodes();
                    for (int j = 0; j < usersId.getLength(); j++) {
                        Node value = usersId.item(j);
                        switch (value.getNodeName()) {
                            case "userId" :
                                for (int k = 0; k < usersId.getLength(); k++) {
                                    dialog.addUser(Integer.parseInt(value.getTextContent()));
                                }
                                break;
                        }
                    }
                    break;
                case "message" :
                    int authorId = -1;
                    String text = null;
                    ZonedDateTime time = null;

                    NodeList messageInfo = message.getChildNodes();
                    for (int j = 0; j < messageInfo.getLength(); j++) {
                        Node value = messageInfo.item(j);
                        switch (value.getNodeName()) {
                            case "authorId":
                                authorId = Integer.parseInt(value.getTextContent());
                                break;
                            case "text":
                                text = value.getTextContent();
                                break;
                            case "time" :
                                time = ZonedDateTime.parse(value.getTextContent());
                                break;
                        }
                    }

                    dialog.addMessage(new UserMessage(authorId, text, time));
                    break;
            }
        }

        return dialog;
    }

    private void initDialogDoc(int id, Document dialog) {
        Element idElement = dialog.createElement("id");
        idElement.appendChild(dialog.createTextNode(Integer.toString(id)));

        Element usersElement = dialog.createElement("users");

        dialog.getDocumentElement().appendChild(idElement);
        dialog.getDocumentElement().appendChild(usersElement);
    }

    @Override
    public int searchAuthenticationData(AuthenticationData authenticationData) {
        log.info("Search: " + authenticationData.getLogin());
        NodeList listData = authenticationDataDoc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < listData.getLength(); i++) {
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

    private User searchUser(int userId) {
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

    private void save(Document document, File file) throws IOException {
        log.info("Saving: " + file.toString());
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
        for (Map.Entry<Integer, Document> entry: dialogDocs.entrySet()) {
            save(entry.getValue(), new File(Integer.toString(entry.getKey()) + ".xml"));
        }
    }

}
