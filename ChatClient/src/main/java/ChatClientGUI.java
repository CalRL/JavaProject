import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

public class ChatClientGUI extends JFrame {
    private EnvVariables envVar = new EnvVariables();
    private PlaceholderTextField usernameField = new PlaceholderTextField("USERNAME");

    public ChatClientGUI() {
        JFrame frame = new JFrame("Chat app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 500);
        frame.setLayout(new BorderLayout(5, 5)); // Small gap between components
        frame.setResizable(false);

        // Top Panel Configuration
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.add(new JLabel("Host Address:"));
        topPanel.add(new JTextField(10));
        topPanel.add(new JLabel("Host Port:"));
        topPanel.add(new JTextField(5));
        topPanel.add(new JLabel("Username:"));
        topPanel.add(new JTextField(10));
        JButton connectButton = new JButton("Connect");
        topPanel.add(connectButton);

        // Middle Panel Configuration
        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add 10-pixel margins on the left and right

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        JList<String> userList = new JList<>(new String[]{"All", "Anuraag", "Rohit"});
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(100, 150));
        middlePanel.add(scrollPane, BorderLayout.CENTER);
        middlePanel.add(userListScrollPane, BorderLayout.EAST);

        // Bottom Panel Configuration
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bottomPanel.add(new JLabel("Message:"));
        JTextField messageField = new JTextField(28); // Adjusted for a better fit
        bottomPanel.add(messageField);
        JButton sendButton = new JButton("Send Message");
        bottomPanel.add(sendButton);

        // Adding panels to the frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);


        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Example of using the getUsername method within this class
                String usernameText = getUsername();
                System.out.println("Attempting to connect with username: " + usernameText);
            }
        });


    }

    // Getter method for username
    public String getUsername() {
        // Ensure the field doesn't just contain the placeholder
        if (usernameField.getText().equals(usernameField.getPlaceholderContent())) {
            return ""; // Or some other default value or throw an exception
        }
        return usernameField.getText();
    }

    // Nested class for a custom JTextField that handles placeholders
    public class PlaceholderTextField extends JTextField {
        private String placeholderContent;

        public PlaceholderTextField(String placeholderContent) {
            super(placeholderContent);
            this.placeholderContent = placeholderContent;
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
        }

        public String getPlaceholderContent() {
            return placeholderContent;
        }
    }

    public static void main(String[] args) {
        new ChatClientGUI();
    }
}
