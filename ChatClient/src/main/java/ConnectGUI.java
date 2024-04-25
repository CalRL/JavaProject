import javax.swing.*;
import java.awt.*;

public class ConnectGUI extends JFrame {
    private JPanel mainPanel;
    private JTextField portField;
    private JTextField usernameField;
    //private JTextField ipField;
    JLabel ipLabel = new JLabel("IP ADDRESS");
    JLabel portLabel = new JLabel("PORT");
    JLabel usernameLabel = new JLabel("USERNAME");
    JTextField ipField = new JTextField();


    public ConnectGUI () {
        JFrame frame = new JFrame();
        setTitle("Connect GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        mainPanel.setLayout(null);
        mainPanel.add(ipLabel, ipField);
        frame.add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);


    }
    public static void main (String[] args) {
        new ConnectGUI();
    }
}
