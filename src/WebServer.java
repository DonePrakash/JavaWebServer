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
    
                // Each client handled by a separate thread
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private static void handleClientRequest(Socket clientSocket) {
        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            String requestLine = reader.readLine();
            System.out.println("Received Request: " + requestLine);
    
            if (requestLine != null && requestLine.startsWith("GET ")) {
                String requestedFile = requestLine.split(" ")[1];
    
                if (requestedFile.equals("/")) {
                    requestedFile = "/index.html";
                }
    
                String filePath = PUBLIC_DIR + requestedFile;
                System.out.println("Requested File Path: " + filePath);
    
                File file = new File(filePath);
                if (file.exists()) {
                    String contentType = getContentType(filePath);
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
    
                    // HTTP headers
                    String header = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + fileBytes.length + "\r\n" +
                            "Connection: close\r\n\r\n";
    
                    output.write(header.getBytes());
                    output.write(fileBytes);
                    output.flush();
                } else {
                    String notFound = "<h1>404 - Page Not Found</h1>";
                    String header = "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: " + notFound.length() + "\r\n\r\n";
                    output.write(header.getBytes());
                    output.write(notFound.getBytes());
                }
            } else {
                String badRequest = "<h1>400 - Bad Request</h1>";
                String header = "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + badRequest.length() + "\r\n\r\n";
                output.write(header.getBytes());
                output.write(badRequest.getBytes());
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
        if (filePath.endsWith(".json")) return "application/json";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".gif")) return "image/gif";
        if (filePath.endsWith(".mp4")) return "video/mp4";
        if (filePath.endsWith(".mp3")) return "audio/mpeg";
        return "application/octet-stream"; // Default for unknown binary
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
