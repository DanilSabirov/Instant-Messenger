import database.Database;
import database.XMLDatabase;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Database database = new XMLDatabase("/tmp");
        try {
            Server server = new ServerIM(database, 4444);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
