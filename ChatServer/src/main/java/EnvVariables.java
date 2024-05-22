import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvVariables {

    private int port;
    private int webPort;

    public EnvVariables() {
        loadProperties();
    }
    // Load properties from config.properties file, if it doesn't exist, create one with default values
    private void loadProperties() {
        Properties properties = new Properties();
        String fileName = "config.properties";

        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            properties.load(inputStream);

            // Load properties into variables
            this.port = Integer.parseInt(properties.getProperty("server.port", "4444"));
            this.webPort = Integer.parseInt(properties.getProperty("server.webPort", "8080"));

        } catch (IOException e) {
            System.out.println("Properties file not found. Creating default properties file.");
            createDefaultPropertiesFile(fileName);
            this.port = 4444;
            this.webPort = 8080;
        }
    }

    private void createDefaultPropertiesFile(String fileName) {

        Properties properties = new Properties();
        // Default values
        properties.setProperty("server.port", "4444");
        properties.setProperty("server.webPort", "8080");

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            properties.store(outputStream, "Server Configuration");
            System.out.println("Default properties file created: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * Getters and Setters
    */
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }
    // Main method
    public static void main(String[] args) {
        EnvVariables envVariables = new EnvVariables();
    }
}
