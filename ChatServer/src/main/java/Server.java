import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) {
        EnvVariables envVar = new EnvVariables();
        int portNumber = envVar.getPort();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is running on port: " + portNumber);
            while (true) {

            }
        } catch(IOException e) {
            System.err.println("Could not bind to port: "+ portNumber);
            System.exit(1);
        }
    }
}
