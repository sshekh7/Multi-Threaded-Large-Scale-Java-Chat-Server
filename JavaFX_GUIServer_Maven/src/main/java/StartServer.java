import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StartServer extends Application implements Initializable {
    public Button start;
    public Button add_client;
    Stage primaryStage;
    Server serverConnection;
    Client clientConnection;
    int prev = 0;

    ListView<String> listItems, listItems2;
    static ListView<String> print;
    int client_number;

    public void start_server(ActionEvent actionEvent) throws IOException {

        start.setDisable(true);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_server.fxml"));
        Parent root = loader.load();
        MainServer controller = loader.getController();

        serverConnection = new Server(data -> {
            Platform.runLater(()->{
                listItems.getItems().add(data.toString());
                controller.server_listview.getItems().add(data.toString());
//                System.out.println(listItems.getItems());
//                print.setItems(listItems.getItems());
//                MainServer.get_messages(print);
            });
        });


        controller.set_server(serverConnection);
        Scene scene = new Scene(root);
        Stage newStage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        newStage.setScene(scene);
        newStage.setTitle("Server");
        newStage.show();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listItems = new ListView<String>();
        listItems2 = new ListView<String>();
        print = new ListView<>();
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("start_screen.fxml"));
        primaryStage.setTitle("Web Chat");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public void create_new_client(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add_client.fxml"));
        Parent root = loader.load();
        AddClient controller = loader.getController();
        clientConnection = new Client(data->{
            Platform.runLater(()->{
                listItems2.getItems().add(data.toString());
                AddClient.set_clients(listItems2);
                String temp_data = data.toString();
                // *1*1 2 3 +3+
                if(temp_data.charAt(temp_data.length()-1) != '+' && temp_data.charAt(temp_data.length()-1) != '-'){
                    controller.all_clients.getItems().add(data.toString());
                }
                else{
                    controller.refresh_scene(null);
                    makeGUIChanges(temp_data, controller);
                }

                controller.select_clients.getItems().stream().forEach((MenuItem menuItem) -> menuItem.setOnAction(event ->
                {
                    controller.clientListToMsg =
                            controller.select_clients.getItems().stream()
                            .filter(item -> CheckMenuItem.class.isInstance(item) &&
                                    CheckMenuItem.class.cast(item).isSelected())
                                    .map(MenuItem::getText).collect(Collectors.toList());
                })
                ); // forEach closed here
            });
        });
        clientConnection.start();
        controller.set_client(clientConnection);
        Scene scene = new Scene(root);
        Stage newStage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        newStage.setScene(scene);
        newStage.setTitle("Client");
        newStage.show();
    }

    public void makeGUIChanges(String temp_data, AddClient controller){

        if(temp_data.charAt(2) == '*') client_number = Character.getNumericValue(temp_data.charAt(1));
        else if(temp_data.charAt(2) != '*') {
            String num = new StringBuilder().append(temp_data.charAt(1)).append(temp_data.charAt(2)).toString();
            client_number = Integer.parseInt(num);
        }
        System.out.println("Client NUmber: " + client_number);
        ArrayList<Integer> available_clients = new ArrayList<>();
        int flag = 0;
        for(int i = 0; i < temp_data.length(); i++){
            if(temp_data.charAt(i) == '*'){
                flag++;
            }
            else if(flag == 2){
                if(temp_data.charAt(i) == '+' || temp_data.charAt(i) == '-') break;
                else if(temp_data.charAt(i) == ' ') continue;
                else {

                    if(temp_data.charAt(i+1) == ' '){
                        if(Character.getNumericValue(temp_data.charAt(i)) == client_number){
                            continue;
                        }
                        else{
                            available_clients.add(Character.getNumericValue(temp_data.charAt(i)));
                        }
                    }
                    else {
                        String num = new StringBuilder().append(temp_data.charAt(i)).append(temp_data.charAt(i+1)).toString();
                        if(Integer.parseInt( num) == client_number){
                            i = i + 2;
                            continue;
                        }
                        else{
                            available_clients.add(Integer.parseInt(num));
                            i = i + 2;
                        }
                    }



                } // outer else
            } // outer if
        } // for ends

        controller.select_clients.getItems().clear();
        for(int i = 0; i < available_clients.size(); i++){
            CheckMenuItem menuItem = new CheckMenuItem(available_clients.get(i).toString());
            controller.select_clients.getItems().add(menuItem);
        }
        prev = available_clients.size();
        System.out.println("Available: " + available_clients);

    }
}
