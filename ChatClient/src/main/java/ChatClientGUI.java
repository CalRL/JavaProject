import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientGUI extends JFrame {
    private EnvVariables envVar = new EnvVariables();
    private PlaceholderTextField usernameField = new PlaceholderTextField("USERNAME");
    private Socket socket;
    private JTextArea messageArea;
    private PrintWriter out;
    private BufferedReader in;
    private JButton connectButton;  // Make connectButton a class-level field

    public ChatClientGUI() {
        JFrame frame = new JFrame("Chat app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 500);
        frame.setLayout(new BorderLayout(5, 5));
        frame.setResizable(false);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (socket != null && !socket.isClosed() && out != null) {
                        out.println("/exit"); // Send a proper disconnection signal
                        out.flush();
                        socket.shutdownOutput(); // Close output stream to indicate no more data will be sent
                        try {
                            Thread.sleep(100); // Optional: wait a bit to ensure the data is sent
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        socket.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    dispose();
                    System.exit(0);
                }
            }
        });

        JTextField ipField = new JTextField(envVar.getIpAddress(), 10);
        JTextField portField = new JTextField(String.valueOf(envVar.getPort()), 5);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.add(new JLabel("Host Address:"));
        topPanel.add(ipField);
        topPanel.add(new JLabel("Host Port:"));
        topPanel.add(portField);
        topPanel.add(new JLabel("Username:"));
        topPanel.add(usernameField); // Use the custom PlaceholderTextField for username
        connectButton = new JButton("Connect");
        topPanel.add(connectButton);

        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        messageArea = new JTextArea() {
            @Override
            // Override getInsets to add padding to the text area
            public Insets getInsets() {
                return new Insets(10, 10, 10, 10);
            }
        };
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        middlePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bottomPanel.add(new JLabel("Message:"));
        JTextField messageField = new JTextField(28);
        bottomPanel.add(messageField);
        JButton sendButton = new JButton("Send Message");
        bottomPanel.add(sendButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        connectButton.addActionListener(e -> toggleConnection(ipField.getText().trim(), Integer.parseInt(portField.getText().trim()), connectButton));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (out != null) {
                    String message = messageField.getText().trim();
                    if (!message.isEmpty()) {
                        out.println(message);
                        messageField.setText(""); // Clear text field after sending
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Not connected to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (out != null) {
                    String message = messageField.getText().trim();
                    if (!message.isEmpty()) {
                        out.println(message);
                        messageField.setText(""); // Clear text field after sending
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Not connected to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setConnectButtonText(String text) {
        connectButton.setText(text);
    }

    // Get method for username
    public String getUsername() {
        // Ensure the field doesn't just contain the placeholder
        if (usernameField.getText().equals(usernameField.getPlaceholderContent())) {
            return ""; // Or some other default value or throw an exception
        }
        return usernameField.getText();
    }

    private void toggleConnection(String ip, int port, JButton connectButton) {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                new Thread(this::readMessages).start();
                setConnectButtonText("Disconnect");
                if (!usernameField.getText().equals(usernameField.getPlaceholderContent())) {
                    out.println(usernameField.getText().trim());  // Send the username
                }
            } else {
                closeConnection();
                setConnectButtonText("Connect");
            }
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeConnection() {
        if (socket != null && !socket.isClosed()) {
            try {
                out.println("/exit");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                String msg = message;
                SwingUtilities.invokeLater(() -> messageArea.append(msg + "\n"));
                // Scroll to the bottom of the text area
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Disconnected from server.", "Connection Error", JOptionPane.ERROR_MESSAGE));
        } finally {
            closeConnection();
            setConnectButtonText("Connect");
        }
    }

    public static void main(String[] args) {
        new ChatClientGUI();
    }
}
