import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) {
        int portNumber = 4444;
        serverSocket = null;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
        } catch(IOException e) {
            System.err.println("Could not bind to port: "+ portNumber);
            System.exit(1)
        }
    }
}
