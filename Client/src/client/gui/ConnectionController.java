package client.gui;

import javafx.fxml.FXMLLoader;

public class ConnectionController {
    public FXMLLoader getLoader() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/fxml/connection.fxml"));
        loader.setController(this);
        return loader;
    }
}
