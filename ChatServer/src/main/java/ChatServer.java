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
                if (username != null && !username.isEmpty()) {
                    usernames.add(username);

                }
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    if(inputLine.startsWith("/")) {
                        handleCommand(inputLine);
                    } else {
                        processMessage(username, inputLine);
                    }
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

        private void handleCommand(String command) {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0];
            String message = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "/broadcast":
                    broadcastMessage("Broadcast from " + username + ": " + message);
                    System.out.println("Broadcasted from" + username);
                    logMessage("Broadcast from" + username);
                    break;
                case "/exit":
                case "/disconnect":
                    broadcastMessage(username + " has left the chat");

                    closeConnection();
                    break;

                case "/coinflip":
                    processMessage(username, flip());
                    logMessage("dsa used /coinflip and got: " + flip());



                    System.out.println(username + " used /coinflip");
                    break;
                case "/userlist":
                    broadcastUserList();
                    System.out.println(username + " used /userlist");
                    break;
                default:
                    writer.println("Unknown command: " + cmd);
            }
        }
        public String flip() {
            double random = Math.random();
            String text = "";
            if (random < 0.5) {
                text = " got Heads in /coinflip!";
            } else {
                text = " got Tails in /coinflip!";
            }
            return text;
        }


        private void processMessage(String username, String message) {

            Date now = new Date();
            String dateFormatter = new SimpleDateFormat("dd/MM HH:mm:ss").format(now);
            String formattedMessage = " [" + dateFormatter + "] " + username + ": " + message;
            System.out.println("Received: " + formattedMessage);
            broadcastMessage(formattedMessage);

        }
    }

    private void logMessage(String message) {
        String logFileDate = new SimpleDateFormat("dd-MM").format(new Date());
        String logFileName = "chat-" + logFileDate + ".log";
        File logFile = new File(logFileName);
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
    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.writer.println(message);
            logMessage(message);
        }
    }

    private void broadcastUserList() {
        String userList = String.join(",", usernames);
        for (ClientHandler client : clients) {
            client.writer.println("User List: " + userList);
        }
    }


    public static void main(String[] args) {
        EnvVariables envPort = new EnvVariables();
        //Open thread for ChatServer
        new Thread(() -> new ChatServer().start(envPort.getPort())).start();
        // Start HTTP Server
        try {
            StatsHttpServer.startHttpServer(envPort.getWebPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
