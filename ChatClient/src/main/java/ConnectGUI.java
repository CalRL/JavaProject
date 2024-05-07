import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

public class ConnectGUI extends JFrame {
    EnvVariables envVar = new EnvVariables();





    public ConnectGUI () {

        JFrame frame = new JFrame();

        JLabel ipLabel = new JLabel("IP ADDRESS");
        JLabel portLabel = new JLabel("PORT");
        JLabel usernameLabel = new JLabel("USERNAME");
        PlaceholderTextField ipField = new PlaceholderTextField("IP ADDRESS");
        PlaceholderTextField portField = new PlaceholderTextField("PORT");
        PlaceholderTextField usernameField = new PlaceholderTextField("USERNAME");

        JButton connectButton = new JButton("Connect");
        JDialog connectStatus = new JDialog();

        String usernameText, ipText, portText;
        ipText = ipField.getText();
        portText = portField.getText();
        usernameText = usernameField.getText();






        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {




                JLabel statusLabel = new JLabel("status is null " + ipText + " " + portText + " " + usernameText);
                connectStatus.setTitle("Status");
                setSize(300, 200);
                add(statusLabel);
                pack();
                setVisible(true);
            }
        });


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(ipLabel);
        mainPanel.add(ipField);
        mainPanel.add(portLabel);
        mainPanel.add(portField);
        mainPanel.add(usernameLabel);
        mainPanel.add(usernameField);
        mainPanel.add(connectButton);


        frame.setSize(600, 500);
        frame.getContentPane().add(mainPanel);
        pack();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);






    }
    public class PlaceholderTextField extends JTextField {
        private String placeholderContent;

        public PlaceholderTextField(String placeholderContent) {
            this.placeholderContent = placeholderContent;
            setText(placeholderContent);

            addFocusListener(new FocusAdapter() {

                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholderContent)) {
                        setText("");
                    }
                }


                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholderContent);
                    }
                }
            });
        }}
    public static void main (String[] args) {
        new ConnectGUI();

    }
}
