import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageData implements Serializable {
    public String message; // message to be sent
    public boolean sendAll; // should the message be sent to all clients
    public List<String> clientListToMsg; // list of clients to send the message to
    public ArrayList<Integer> clientNumToMsg; // list of client numbers to send the message to


    public MessageData() {
        this.message = "";
        this.sendAll = false; // assumed that client doesn't want to send a message to all the others
        this.clientListToMsg = new ArrayList<>();
        this.clientNumToMsg = new ArrayList<>();
    }
}
