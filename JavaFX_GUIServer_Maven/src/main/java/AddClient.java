import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class AddClient implements Initializable {
    public ListView all_clients; // display messages here
    public TextField client_message; // get the message to send
    public Button send_button;
    public CheckBox select_all; // send the message to all clients
    public ListView client_messages; // display clients that are connected currently? maybe not useful rn?
    public MenuButton select_clients; // select which clients to send the message to
    public Label client_num_text;
    List<String> clientListToMsg; // list of clients to send the message to
    ObservableList<String> items = FXCollections.observableArrayList();
    ObservableList<CheckMenuItem> client_options = FXCollections.observableArrayList();
    int client_number;
    Client clientConnection;
    ArrayList<String> send_options;

    public ArrayList<String> active_clients;
    public ArrayList<String> past_messages;
    public static ArrayList<String> listItems2;
    ArrayList<String> clients_array = new ArrayList<>();

    public void send_message(ActionEvent actionEvent) {
        MessageData msgData = new MessageData();
        msgData.message = client_message.getText();
        msgData.clientListToMsg = clientListToMsg;

        // check if we have to send the message to all clients
        if (select_all.isSelected())
            msgData.sendAll = true;

        // if clientListToMsg is not null, that means that we have to send the message to a select
        // number of clients
        // add these client numbers to clientNumToMsg in msgData
        if (clientListToMsg != null)
        {
            for (int i = 0; i < msgData.clientListToMsg.size(); i++)
            {
                msgData.clientNumToMsg.add(Integer.parseInt(msgData.clientListToMsg.get(i)));
            } // for loop ends
        } // if ends

        clientConnection.send(msgData);
        client_message.clear();
        refresh_scene(actionEvent);
    }

    public static void set_clients(ListView<String> items){
        listItems2.addAll(items.getItems());
    }


    public void set_client(Client clientConnection) {
        this.clientConnection = clientConnection;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        active_clients = new ArrayList<>();
        listItems2 = new ArrayList<>();
        client_messages.setItems(items);
        past_messages = new ArrayList<>();
        send_options = new ArrayList<>();
    }

    public void refresh_scene(ActionEvent actionEvent) {
        System.out.println(listItems2);
        items.clear();
        clients_array.clear();
        String temp = "";
        for(int i = 0; i < listItems2.size(); i++){
            if(listItems2.get(i).charAt(listItems2.get(i).length()-1) == '+' || listItems2.get(i).charAt(listItems2.get(i).length()-1) == '-'){
                temp = listItems2.get(i);
            }
        }

        for(int i = 0; i < temp.length(); i++){
            String client = "Client #";
            if(temp.charAt(i) == '+' || temp.charAt(i) == '-'){
                break;
            }
            else if(temp.charAt(i) == ' '){
                continue;
            }
            else if(temp.charAt(i) == '*'){
                if(temp.charAt(i+2) == '*') {
                    client_number = Integer.parseInt(String.valueOf(temp.charAt(i + 1)));
                    i += 2;
                }
                else if(temp.charAt(i+3) == '*') {
                    client_number = Integer.parseInt(temp.charAt(i + 1) + String.valueOf(temp.charAt(i+2)));
                    i += 3;
                }
//                System.out.println("I reached here " + client_number);
            }
            else{
                if(temp.charAt(i+1) == ' '){
                    if(Integer.parseInt(String.valueOf(temp.charAt(i))) == client_number){
                        continue;
                    }
                    else{
                        send_options.add(String.valueOf(temp.charAt(i)));
                        client += temp.charAt(i);
                        clients_array.add(client);
                    }
                }
                else {
                    String num = new StringBuilder().append(temp.charAt(i)).append(temp.charAt(i+1)).toString();
//                    System.out.println("Temp String: " + temp);
//                    System.out.println("I value: " + i);
//                    System.out.println(num);
//                    System.out.println("CLIENT NUM" + client_number);
                    if(Integer.parseInt( num) == client_number){
                        i = i + 2;
                        continue;
                    }
                    else{
                        send_options.add(num);
                        client += num;
                        clients_array.add(client);
                        i = i + 2;
                    }

                }

            }

        }
        client_num_text.setText("Client "+client_number);
        System.out.println("Available Clients: ");
        System.out.println(clients_array);
        items.addAll(clients_array);
        client_messages.setItems(items);
    }


}
