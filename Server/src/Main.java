import database.AuthenticationData;
import database.Database;
import database.XMLDatabase;
import database.user.UserIM;

import java.io.IOException;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        initLog();

        Database database = new XMLDatabase("/tmp");
        database.init();
      //  database.addUser(new UserIM(0, "Balda", "gmail.com"), new AuthenticationData("Balda", "123456".toCharArray()));
        try {
            database.saveAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
