import java.io.*;
import java.net.*;
import java.nio.file.*;

public class WebServer {
    private static final String PUBLIC_DIR = "public"; // Folder for serving static files
    private static final int PORT = 3500;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true)
        ) {
            // Read HTTP request
            String requestLine = reader.readLine();
            System.out.println("Received Request: " + requestLine);

            if (requestLine != null && requestLine.startsWith("GET ")) {
                String requestedFile = requestLine.split(" ")[1];

                // Default route to "/index.html"
                if (requestedFile.equals("/")) {
                    requestedFile = "/index.html";
                }

                String filePath = PUBLIC_DIR + requestedFile;
                
                System.out.println("Requested File Path: " + filePath);
                // Check if file exists
                if (Files.exists(Paths.get(filePath))) {
                    String contentType = getContentType(filePath);
                    sendResponse(writer, 200, "OK", contentType, loadFileContent(filePath));
                } else {
                    sendResponse(writer, 404, "Not Found", "text/html", "<h1>404 - Page Not Found</h1>");
                }

            } else {
                sendResponse(writer, 400, "Bad Request", "text/html", "<h1>400 - Bad Request</h1>");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load file content
    private static String loadFileContent(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            return "<h1>Error Loading Page</h1>";
        }
    }

    // Determine content type based on file extension
    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        return "text/plain";
    }

    // Send HTTP response
    private static void sendResponse(PrintWriter writer, int statusCode, String statusText,
                                     String contentType, String content) {
        writer.println("HTTP/1.1 " + statusCode + " " + statusText);
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + content.length());
        writer.println();
        writer.println(content);
    }
}
