import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static ArrayList<ClientThread> clients;
    EnvVariables envVar = new EnvVariables();
    public static void acceptClients() {
        clients = new ArrayList<>();
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                ClientThread client = new ClientThread(socket);
                Thread thread = new Thread(client);
                thread.start();
                clients.add(client);

            } catch(IOException e) {
                System.out.println("Accept failed " + envVar.getPort());
            }

        }
    }


    public static void main(String[] args) {

        int portNumber = envVar.getPort();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is running on port: " + portNumber);
            acceptClients();
        } catch(IOException e) {
            System.err.println("Could not bind to port: "+ portNumber);
            System.exit(1);
        }
    }
}
