import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MessageBroadcaster {
    private List<ClientHandler> clients;
    private PrintWriter fileOut;

    public MessageBroadcaster(List<ClientHandler> clients) {
        this.clients = clients;
        try {
            fileOut = new PrintWriter(new FileWriter("chatlog.txt", true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
        fileOut.println(message);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
