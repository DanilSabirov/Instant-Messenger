package client;

import client.gui.AuthorizationController;
import client.gui.ConnectionController;
import client.gui.MainController;
import client.gui.RegistrationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.logging.LogManager;

public class Main extends Application{
    private static Client client;

    private static Stage stage;

    private static InetAddress host;

    public static void main(String[] args) throws UnknownHostException {
        loadConfig();
        System.out.println("Address: " + host.toString());
        initLog();

        client = new ClientIM(InetAddress.getLocalHost(), 4444);
        Thread thread = new Thread(() -> {
            while (!client.connect());
        });
        thread.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        setConnectionScene();
    }

    @Override
    public void stop() throws Exception {
        client.closeConnection();
        Runtime.getRuntime().exit(0);
    }

    public static void setAuthorizationScene() {
        AuthorizationController authorization = new AuthorizationController(client);
        try {
            setScene("Login", authorization.getLoader());
        } catch (IOException e) {
            e.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }
    }

    public static void setRegistrationScene() {
        RegistrationController registration = new RegistrationController(client);
        try {
            setScene("Registration", registration.getLoader());
        } catch (IOException e) {
            e.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }
    }

    public static void setMainScene() {
        MainController controller = new MainController(client);
        try {
            setScene("IM", controller.getLoader());
            controller.initWindow();
        } catch (IOException e) {
            e.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }
    }

    public static void setConnectionScene() {
        ConnectionController controller = new ConnectionController();
        try {
            setScene("Connection", controller.getLoader());
        } catch (IOException e) {
            e.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }
    }

    private static void setScene(String title, FXMLLoader loader) throws IOException {
        stage.setTitle(title);
        stage.setScene(new Scene(loader.load()));

        stage.show();
    }

    private static void initLog(){
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/client/logging.properties"));
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
    }

    private static void loadConfig() {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader(new File("config.txt"));
            bufferedReader = new BufferedReader(reader);
        } catch (FileNotFoundException e) {
            try {
                host = InetAddress.getLocalHost();
                return;
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
        }


        try {
            host = InetAddress.getByName(bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
