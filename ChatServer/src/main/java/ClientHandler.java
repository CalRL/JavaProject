import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket socket;
    private MessageBroadcaster broadcaster;
    private PrintWriter out;

    public ClientHandler(Socket socket, MessageBroadcaster broadcaster) {
        this.socket = socket;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run() {
        try {
            Scanner input = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (input.hasNextLine()) {
                String message = input.nextLine();
                broadcaster.broadcastMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            broadcaster.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}