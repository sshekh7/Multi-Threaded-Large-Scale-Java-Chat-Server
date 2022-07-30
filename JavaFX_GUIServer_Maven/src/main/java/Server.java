import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{
	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	Server(Consumer<Serializable> call){
	synchronized (this){
			callback = call;
			server = new TheServer();
			server.start();
		}
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
			synchronized (this){
				try(ServerSocket mysocket = new ServerSocket(5555);){
					System.out.println("Server is waiting for a client!");



					while(true) {

						ClientThread c = new ClientThread(mysocket.accept(), count);
						callback.accept("client has connected to server: " + "client #" + count);

						clients.add(c);
						c.start();

						count++;

					}
				}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}

		}
	

		class ClientThread extends Thread {

			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;

			ClientThread(Socket s, int count) {
				this.connection = s;
				this.count = count;
			}

			public void updateClients(String message, int flag) {
				String temp = "";
				if (flag != 0) {
					for (int i = 0; i < clients.size(); i++) {
						temp += clients.get(i).count + " ";
					}
					if (flag == 1) {
						temp += "+" + message + "+";
					}
					if (flag == 2) {
						temp += "-" + message + "-";
					}
				} else temp = message;

				for (int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
						if (flag == 0) t.out.writeObject(temp);
						else {
							String specific_client_num = "*" + clients.get(i).count + "*" + temp;
							t.out.writeObject(specific_client_num);
						}
					} catch (Exception e) {
					}
				}
			}

			public void sendToSelectClients(String message, MessageData receiveData, int clientNum) throws IOException {
				int i = 0;
				int clientNumToMsgIndex = 0; // refer to clientNumToMsg in MessageData

				// display the message sent by clientNum in their window by finding the correct count in clients (ArrList)
				while (true) {
					if (clients.get(i).count == clientNum) {
						clients.get(i).out.writeObject(message);
						break;
					}

					i++;
				} // while loop ends

				i = 0;

				// loop through clients and check to which clients do we need to send the message sent by clientNum
				// cross-check using clientNumToMsg from MessageData
				while (clientNumToMsgIndex < receiveData.clientNumToMsg.size()) {
					if (receiveData.clientNumToMsg.get(clientNumToMsgIndex) == clients.get(i).count) {
						clients.get(i).out.writeObject(message);
						clientNumToMsgIndex++;
					}

					i++;
				} // while loop ends
			}

			public void run()
			{
				synchronized (this)
				{
					try {
						in = new ObjectInputStream(connection.getInputStream());
						out = new ObjectOutputStream(connection.getOutputStream());
						connection.setTcpNoDelay(true);
					} catch (Exception e) {
						System.out.println("Streams not open");
					}

					updateClients(Integer.toString(count), 1);
					updateClients("new client on server: client #" + count, 0);

					while (true) {
						try {
	//					    	String data = in.readObject().toString();
							MessageData receiveData = (MessageData) in.readObject();
							callback.accept("client: " + count + " sent: " + receiveData.message);

							// if the message is to be sent to all clients
							if (receiveData.sendAll)
								updateClients("client #" + count + " said: " + receiveData.message, 0);

							// if the message is to be sent to selected clients
							if (!receiveData.sendAll)
								sendToSelectClients("client #" + count + " said: " + receiveData.message, receiveData, count);

						} catch (Exception e) {
							callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
							clients.remove(this);
							updateClients(Integer.toString(count), 2);
							updateClients("Client #" + count + " has left the server!", 0);
							break;
						}
					}
				}

			} //end of run
		}//end of client thread
}


	
	

	
