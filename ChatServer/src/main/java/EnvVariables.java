public class EnvVariables {

    private int port = 4444;
    private String ipAddress = "localhost";
    private int webPort = 8080;

    public String getIpAddress() {
        return ipAddress;
    }
/*
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
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
}
