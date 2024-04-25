import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        int portNumber = 4444;
        try {
            socket = new Socket("localhost", portNumber);
        } catch (IOException e) {
            System.err.println("Connection Error");
            System.exit(1);
        }
/*
        public void run() {
            ConnectGUI gui = new ConnectGUI();
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(gui);
            frame.pack();
            frame.setVisible(true);
        }*/
    }
}
