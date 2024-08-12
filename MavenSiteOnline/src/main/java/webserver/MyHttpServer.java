package webserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyHttpServer {

    public static void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado na porta " + port);
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = "\"C:\\xampp\\htdocs\\index.html\"";
            String response;
            
            if (Files.exists(Paths.get(filePath))) {
                response = new String(Files.readAllBytes(Paths.get(filePath)));
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length());
            } else {
                response = "<h1>404 Not Found</h1>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(404, response.length());
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public static void main(String[] args) {
        try {
            startServer(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
