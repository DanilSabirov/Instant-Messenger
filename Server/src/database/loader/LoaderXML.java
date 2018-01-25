package database.loader;

import database.XMLDatabase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class LoaderXML{
    private static Logger log = Logger.getLogger(XMLDatabase.class.getName());

    private  DocumentBuilderFactory factory;

    private  DocumentBuilder builder;

    public LoaderXML() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    public Document load(Path pathToDirectory, File fileXML, String rootElement) throws IOException, SAXException {
        if (Files.exists(pathToDirectory) && Files.isDirectory(pathToDirectory)) {
            if (new File(pathToDirectory.toFile(), fileXML.toString()).exists()) {
                log.info("Loading: " + fileXML.toString());
                return builder.parse(new FileInputStream(new File(pathToDirectory.toFile(), fileXML.toString())));
            }
            else{
                log.info("Creating: " + fileXML.toString());
                Document userDoc = builder.newDocument();
                Element usersElement = userDoc.createElement(rootElement);
                userDoc.appendChild(usersElement);

                return userDoc;
            }
        }
        throw new FileNotFoundException();
    }

}
