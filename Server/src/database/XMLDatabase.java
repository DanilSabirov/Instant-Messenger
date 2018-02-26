package database;

import database.dialog.Dialog;
import database.dialog.GroupDialog;
import database.loader.LoaderXML;
import database.message.Message;
import database.message.UserMessage;
import database.user.User;
import database.user.UserIM;
import javafx.scene.Parent;
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private int sequenceUserId = 0;

    private int sequenceDialogId = 0;

    public XMLDatabase(String pathToRoot) {
        this.pathToRoot = Paths.get(pathToRoot);
        dialogDocs = new TreeMap<>();
    }

    @Override
    public void init() {
        try {
            loaderXML = new LoaderXML();
            factory = DocumentBuilderFactory.newInstance();
            documentBuilder = factory.newDocumentBuilder();

            usersXML = new File("users.xml");
            authenticationDataXML = new File("authenticationData.xml");

            usersDoc = loaderXML.load(pathToRoot, usersXML, "users");
            authenticationDataDoc = loaderXML.load(pathToRoot, authenticationDataXML, "authenticationData");

            initSequence();

            saveAll();
        } catch (ParserConfigurationException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        } catch (SAXException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    private void initSequence() {
        if (!authenticationDataDoc.getDocumentElement().hasAttributes()) {
            authenticationDataDoc.getDocumentElement().setAttribute("lastUserId", Integer.toString(0));
            authenticationDataDoc.getDocumentElement().setAttribute("lastDialogId", Integer.toString(0));
        }
        else {
            sequenceUserId = Integer.parseInt(authenticationDataDoc.getDocumentElement().getAttribute("lastUserId"));
            sequenceDialogId = Integer.parseInt(authenticationDataDoc.getDocumentElement().getAttribute("lastDialogId"));
        }
    }

    @Override
    public boolean addUser(User user, AuthenticationData authenticationData) {
        Element userElement = usersDoc.createElement("user");
        userElement.setAttribute("id", Integer.toString(user.getId()));

        Element nameElement = usersDoc.createElement("name");
        nameElement.appendChild(usersDoc.createTextNode(user.getName()));

        Element emailElement = usersDoc.createElement("email");
        emailElement.appendChild(usersDoc.createTextNode(user.getEmail()));

        Element dialogsIdElement = usersDoc.createElement("dialogsId");

        usersDoc.getDocumentElement().appendChild(userElement);
        userElement.appendChild(nameElement);
        userElement.appendChild(emailElement);
        userElement.appendChild(dialogsIdElement);

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
        dialogDoc.getDocumentElement().setAttribute("id", Integer.toString(dialog.getId()));

        Element usersElement = dialogDoc.createElement("users");
        dialogDoc.getDocumentElement().appendChild(usersElement);

        for (Integer userId: dialog.getUsersId()) {
            addNewUserToDialogDoc(dialogDoc, userId);
            addNewDialogToUserDoc(dialog.getId(), userId);
        }
        log.info("Created dialog: " + dialog.getId() + dialog.getUsersId());
    }

    @Override
    public void addNewUserToDialog(int dialogId, int userId) throws IOException, SAXException {
        if (!dialogDocs.containsKey(dialogId)) {
            dialogDocs.put(dialogId, getDialogDoc(dialogId));
        }

        Document dialogDoc = dialogDocs.get(dialogId);

        addNewUserToDialogDoc(dialogDoc, userId);
        addNewDialogToUserDoc(dialogId, userId);
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

    private void addNewDialogToUserDoc(int dialogId, int userId) throws IOException, SAXException {
        NodeList users = usersDoc.getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < users.getLength(); i++) {
            Node user = users.item(i);
            if (user.getNodeName().equals("user") && user.getAttributes().getNamedItem("id").getNodeValue().equals(Integer.toString(userId))) {
                NodeList values = user.getChildNodes();
                for(int j = 0; j < values.getLength(); j++) {
                    Node value = values.item(j);
                    if (value.getNodeName().equals("dialogsId")) {
                        Element newDialog = usersDoc.createElement("dialogId");
                        newDialog.appendChild(usersDoc.createTextNode(Integer.toString(dialogId)));
                        value.appendChild(newDialog);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void addMessage(Message message) throws IOException, SAXException {
        int dialogId = message.getDialogId();

        if (!dialogDocs.containsKey(dialogId)) {
            dialogDocs.put(dialogId, getDialogDoc(dialogId));
        }

        Document dialogDoc = dialogDocs.get(dialogId);

        Element messageElement = dialogDoc.createElement("message");

        Element authorIdElement = dialogDoc.createElement("authorId");
        authorIdElement.appendChild(dialogDoc.createTextNode(Integer.toString(message.getAuthorId())));

        Element authorNameElement = dialogDoc.createElement("authorName");
        authorNameElement.appendChild(dialogDoc.createTextNode(message.getAuthorName()));

        Element textElement = dialogDoc.createElement("text");
        textElement.appendChild(dialogDoc.createTextNode(message.getText()));

        Element timeElement = dialogDoc.createElement("time");
        timeElement.appendChild(dialogDoc.createTextNode(message.getDateReceipt().toString()));

        messageElement.appendChild(authorIdElement);
        messageElement.appendChild(authorNameElement);
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

        dialog.setId(Integer.parseInt(dialogDoc.getDocumentElement().getAttribute("id")));

        NodeList nodeList = dialogDoc.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("users")){
                NodeList usersId = node.getChildNodes();
                for (int j = 0; j < usersId.getLength(); j++) {
                    if (usersId.item(j).getNodeName().equals("userId")){
                        dialog.addUser(Integer.parseInt(usersId.item(j).getTextContent()));
                    }
                }
                break;
            }
        }

        for (int i = 0; i < messageList.getLength(); i++) {
            Node message = messageList.item(i);
            switch (message.getNodeName()) {
                case "message" :
                    int authorId = -1;
                    String authorName = null;
                    String text = null;
                    ZonedDateTime time = null;

                    NodeList messageInfo = message.getChildNodes();
                    for (int j = 0; j < messageInfo.getLength(); j++) {
                        Node value = messageInfo.item(j);
                        switch (value.getNodeName()) {
                            case "authorId":
                                authorId = Integer.parseInt(value.getTextContent());
                                break;
                            case "authorName":
                                authorName = value.getTextContent();
                            case "text":
                                text = value.getTextContent();
                                break;
                            case "time" :
                                time = ZonedDateTime.parse(value.getTextContent());
                                break;
                        }
                    }

                    dialog.addMessage(new UserMessage(authorId, authorName, dialog.getId(), text, time));
                    break;
            }
        }

        return dialog;
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
    public List<User> searchUsers(String namePrefix) {
        log.info("Search: " + namePrefix + '*');
        List<User> users = new ArrayList<>();
        NodeList listData = usersDoc.getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < listData.getLength(); i++){
            if (listData.item(i).getNodeName().equals("user")) {
                NodeList values = listData.item(i).getChildNodes();

                int id = Integer.parseInt( ((Element) listData.item(i)).getAttribute("id"));
                String name = null;
                String email = null;
                List<Integer> dialogsIdSet = new ArrayList<>();

                boolean ok = true;
                for(int j = 0; j < values.getLength(); j++) {
                    Node value = values.item(j);
                    if (value.getNodeName().equals("name")) {
                        name = value.getTextContent();
                        if (!name.contains(namePrefix)){
                            ok = false;
                            break;
                        }
                    }
                    else if (value.getNodeName().equals("email")) {
                        email = value.getTextContent();
                    }
                }

                if (ok){
                    log.info("Found: " + id + " " + name);
                    users.add(new UserIM(id, name, email, dialogsIdSet));
                }
            }
        }
        if (users.size() == 0) {log.info("Not found!");}
        return users;
    }

    private User searchUser(int userId) {
        log.info("Search: " + userId);
        NodeList listData = usersDoc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < listData.getLength(); i++){
            if (listData.item(i).getNodeName().equals("user")) {
                NodeList values = listData.item(i).getChildNodes();

                int id = Integer.parseInt( ((Element) listData.item(i)).getAttribute("id"));
                if (id == userId) {
                    String name = null;
                    String email = null;
                    List<Integer> dialogsIdList = new ArrayList<>();

                    for(int j = 0; j < values.getLength(); j++) {
                        Node value = values.item(j);
                        if (value.getNodeName().equals("name")) {
                            name = value.getTextContent();
                        }
                        else if (value.getNodeName().equals("email")) {
                            email = value.getTextContent();
                        }
                        else if (value.getNodeName().equals("dialogsId")) {
                            NodeList dialogsId = value.getChildNodes();
                            for (int k = 0; k < dialogsId.getLength(); k++) {
                                Node dialogId = dialogsId.item(k);
                                if (dialogId.getNodeName().equals("dialogId")) {
                                    Integer.parseInt(dialogId.getTextContent());
                                    dialogsIdList.add(Integer.parseInt(dialogId.getTextContent()));
                                }
                            }
                        }
                    }

                    log.info("Found: " + id + " " + name);
                    return new UserIM(id, name, email, dialogsIdList);
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

    @Override
    public int getSequenceUserId() {
        authenticationDataDoc.getDocumentElement().setAttribute("lastUserId", Integer.toString(++sequenceUserId));
        return sequenceUserId;
    }

    @Override
    public int getSequenceDialogId() {
        authenticationDataDoc.getDocumentElement().setAttribute("lastDialogId", Integer.toString(++sequenceDialogId));
        return sequenceDialogId;
    }

}
