import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
public class ChatServer {
    private ServerSocket serverSocket;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // First message from the client is expected to be the username
                username = reader.readLine();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    processMessage(username, inputLine);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        private void closeConnection() {
            try {
                clients.remove(this);
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }

        private void processMessage(String username, String message) {

            Date now = new Date();
            String dateFormatter = new SimpleDateFormat("dd/MM HH:mm:ss").format(now);
            String formattedMessage = " [" + dateFormatter + "] " + username + ": " + message;
            System.out.println("Received: " + formattedMessage);
            broadcastMessage(formattedMessage);
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.writer.println(message);
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(4444);
    }
}
