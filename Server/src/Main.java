import database.Database;
import database.XMLDatabase;
import database.dialog.GroupDialog;
import database.message.UserMessage;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        initLog();

        Database database = new XMLDatabase("/tmp");
        database.init();

//        try {
//            Set<Integer> list = new TreeSet<>();
//            list.add(1);
//            list.add(2);
//            database.createDialog(new GroupDialog(1, list));
//            database.addMessage(new UserMessage(1, "Hello World!!!", ZonedDateTime.now()), 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }
//        try {
//            database.saveAll();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            Server server = new ServerIM(database, 4444);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            database.saveAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initLog(){
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
    }
}
