import java.io.IOException;
import java.net.Socket;

public class Client {
    static EnvVariables envVar = new EnvVariables();
    static int portNumber = envVar.getPort();
    static String ipAddress = envVar.getIpAddress();
    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket(ipAddress, portNumber);

            Thread server = new Thread(new ServerThread(socket));
            server.start();
        } catch (IOException e) {
            System.err.println("IOException Error");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
