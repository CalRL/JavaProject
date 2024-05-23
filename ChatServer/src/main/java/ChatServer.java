import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatServer {
    private ServerSocket serverSocket;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> usernames = new CopyOnWriteArrayList<>();

    // Start function for the server
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

    public int getUserCount() {
        return clients.size();
    }

    public CopyOnWriteArrayList<String> getUsernames() {
        return usernames;
    }

    // Initialize the reader and writer.
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
            /* Username field is the first line sent to the server
             *  If the username is empty, the first message the client sends will be their username
             */
            try {
                // First message from the client is expected to be the username
                username = reader.readLine();
                if (username != null && !username.isEmpty()) {
                    usernames.add(username);
                    System.out.println(username + " has joined the chat");
                    broadcastMessage(username + " has joined the chat");
                }
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    if (inputLine.startsWith("/")) {
                        handleCommand(inputLine);
                    } else {
                        processMessage(username, inputLine);
                    }
                }
            } catch (IOException e) {
                System.err.println(username  + " has disconnected: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        /* Close the connection this way
         * This triggers the "Error handling client:" IOException as seen above, this is intended.
         */
        private void closeConnection() {
            try {
                clients.remove(this);
                if (username != null) {
                    usernames.remove(username);
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }

        /*
         * Basic command handler using a switch statement
         * parts[0] is the command and parts[1] is the args (if any),
         * this is built to be modifiable and ready to work with args
         * /broadcast sends a message to all clients in a different format
         * /exit and /disconnect closes the connection
         * /coinflip does a coinflip
         * /userlist sends a list of all users in the chat
         */
        private void handleCommand(String command) {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0];
            String message = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "/broadcast":
                    broadcastMessage("Broadcast from " + username + ": " + message);
                    System.out.println("Broadcasted from " + username);
                    logMessageToFile("Broadcast from" + username);
                    break;
                case "/exit":
                case "/disconnect":
                    broadcastMessage(username + " has left the chat");
                    closeConnection();
                    break;

                case "/coinflip":
                    broadcastMessage(flip());
                    System.out.println(username + " used /coinflip");
                    logMessageToFile(username + " used /coinflip and got: " + flip());
                    break;
                case "/userlist":
                    broadcastUserList();
                    System.out.println(username + " used /userlist");
                    logMessageToFile(username + " used /userlist");
                    break;
                default:
                    writer.println("Unknown command: " + cmd);
            }
        }

        public String flip() {
            double random = Math.random();
            String text = "";
            if (random < 0.5) {
                text = "Heads";
            } else {
                text = "Tails";
            }
            return text;
        }

        private void processMessage(String username, String message) {
            Date now = new Date();
            String dateFormatter = new SimpleDateFormat("dd/MM HH:mm:ss").format(now);
            String formattedMessage = "[" + dateFormatter + "] " + username + ": " + message;
            System.out.println("Received: " + formattedMessage);
            broadcastMessage(formattedMessage);
        }
    }

    /*
     * Broadcast the message to all clients
     * logMessage() logs the same message
     */
    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.writer.println(message);
        }
        logMessageToFile(message);
    }

    private void broadcastUserList() {
        String userList = "Users: " + String.join(", ", usernames);
        for (ClientHandler client : clients) {
            client.writer.println("/userlist " + userList);
        }
    }
    // Log messages to a file
    private void logMessageToFile(String message) {
        String logFileDate = new SimpleDateFormat("dd-MM").format(new Date());
        String logFileName = "chat-" + logFileDate + ".log";
        File logFile = new File("./" + logFileName);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();

            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EnvVariables envPort = new EnvVariables();
        ChatServer server = new ChatServer();

        // Open thread for ChatServer
        new Thread(() -> server.start(envPort.getPort())).start();

        // Start HTTP Server
        try {
            StatsHttpServer.startHttpServer(envPort.getWebPort(), server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
