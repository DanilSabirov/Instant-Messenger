import database.Database;
import database.XMLDatabase;

import java.io.IOException;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        initLog();

        Database database = new XMLDatabase("/tmp");
        try {
            Server server = new ServerIM(database, 4444);
            server.start();
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
