package client;

import client.gui.AuthorizationController;
import client.gui.MainController;
import client.gui.RegistrationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.LogManager;

public class Main extends Application{
    private static Client client;

    private static Stage stage;

    public static void main(String[] args) throws UnknownHostException {
        initLog();
        client = new ClientIM(InetAddress.getLocalHost(), 4444);
        Thread thread = new Thread(() -> {
            if (!client.connect()) {
                Runtime.getRuntime().exit(-1);
            }
        });
        thread.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
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
            controller.setUserInfo();
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
}
