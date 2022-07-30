import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainServer implements Initializable {
    public ListView server_listview;
    public Server serverConnection;
    static ObservableList<String> all_messages = FXCollections.observableArrayList();
    public Button refresh_button;

    public void set_server(Server serverConnection){
        this.serverConnection = serverConnection;
    }

    public static void get_messages(ListView<String> message){
        all_messages = message.getItems();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
