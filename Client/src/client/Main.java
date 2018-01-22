package client;

import client.gui.AuthorizationController;
import client.gui.MainController;
import client.gui.view.AuthorizationWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main extends Application{
    private static Client client;

    private static Stage stage;

    public static void main(String[] args) throws UnknownHostException {
        client = new ClientIM(InetAddress.getLocalHost(), 4444);
        if (!client.connect()) Runtime.getRuntime().exit(-1);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        setAuthorizationScene();
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

    public static void setMainScene() {
        MainController controller = new MainController(client);
        try {
            setScene("IM", controller.getLoader());
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
}
