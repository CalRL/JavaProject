import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.text.SimpleDateFormat;

public class StatsHttpServer {

    private static final Instant startTime = Instant.now();

    // Start the HTTP server
    public static void startHttpServer(int port) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", new StatsHandler());
        httpServer.createContext("/messages", new MessagesHandler());
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
        System.out.println("HTTP server started on port " + port);
    }

    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = generateStatsPage();
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
        // Generate the HTML for the stats page
        private String generateStatsPage() {
            StringBuilder html = new StringBuilder();
            html.append("<html>")
                    .append("<head><title>Server Stats</title>")
                    .append("<script>")
                    .append("function fetchMessages() {")
                    .append("  fetch('/messages').then(response => response.text()).then(data => {")
                    .append("    document.getElementById('messages').innerHTML = data;")
                    .append("  });")
                    .append("}")
                    .append("setInterval(fetchMessages, 2000);") // Fetch messages every 2 seconds
                    .append("</script>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h1>Server Stats</h1>")
                    .append("<p><b>Uptime:</b> <span id='uptime'></span></p>")
                    .append("<h2>Messages:</h2>")
                    .append("<ul id='messages'>");

            // Initial message load
            String messages = readMessagesFromFile();
            for (String message : messages.split("\n")) {
                if (!message.trim().isEmpty()) {
                    html.append("<li>").append(message).append("</li>");
                }
            }

            html.append("</ul>")
                    .append("<script>")
                    .append("function updateUptime() {")
                    .append("  let startTime = ").append(startTime.getEpochSecond()).append(";")
                    .append("  setInterval(function() {")
                    .append("    let now = Math.floor(Date.now() / 1000);")
                    .append("    let uptime = now - startTime;")
                    .append("    let hours = Math.floor(uptime / 3600);")
                    .append("    let minutes = Math.floor((uptime % 3600) / 60);")
                    .append("    let seconds = uptime % 60;")
                    .append("    document.getElementById('uptime').textContent = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;")
                    .append("  }, 1000);")
                    .append("}")
                    .append("updateUptime();")
                    .append("</script>")
                    .append("</body>")
                    .append("</html>");
            return html.toString();
        }
        // Read messages from the log file
        private String readMessagesFromFile() {
            StringBuilder messages = new StringBuilder();
            String logFileDate = new SimpleDateFormat("dd-MM").format(new Date());
            String logFileName = "chat-" + logFileDate + ".log";
            File logFile = new File(logFileName);
            if (logFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        messages.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Log file does not exist: " + logFileName);
            }
            return messages.toString();
        }
    }

    static class MessagesHandler implements HttpHandler {
        @Override
        // Handle the HTTP request and send the messages as a response
        public void handle(HttpExchange exchange) throws IOException {
            String messages = readMessagesFromFile();
            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, messages.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(messages.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
        // Read messages from the log file used in the handle method, and appends the messages to the HTML code
        private String readMessagesFromFile() {
            StringBuilder messages = new StringBuilder();
            String logFileDate = new SimpleDateFormat("dd-MM").format(new Date());
            String logFileName = "chat-" + logFileDate + ".log";
            File logFile = new File(logFileName);
            if (logFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        messages.append("<li>").append(line).append("</li>");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Log file does not exist: " + logFileName);
            }
            return messages.toString();
        }
    }
}
