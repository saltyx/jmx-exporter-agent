package jmx.exporter.agent.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ClosableHttpServer extends HTTPServer {

    public ClosableHttpServer(String host, int port, boolean daemon, CloseHttpServerCallback callback) throws IOException {
        super(host, port, daemon);
        initializeServer(callback);
    }

    public ClosableHttpServer(int port, boolean daemon, CloseHttpServerCallback callback) throws IOException {
        super(port, daemon);
        initializeServer(callback);
    }

    public ClosableHttpServer(int port, CloseHttpServerCallback callback) throws IOException {
        super(port);
        initializeServer(callback);
    }

    private void initializeServer(CloseHttpServerCallback callback) {
        this.server.createContext("/close", new CloseHttpServerHandler(callback, this));
    }

    static class CloseHttpServerHandler implements HttpHandler {

        private CloseHttpServerCallback callback;
        private ClosableHttpServer closableHttpServer;

        CloseHttpServerHandler(CloseHttpServerCallback callback, ClosableHttpServer server) {
            this.callback = callback;
            this.closableHttpServer = server;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            final ByteArrayOutputStream response = new ByteArrayOutputStream(1 << 10);
            final OutputStreamWriter osw = new OutputStreamWriter(response);
            osw.write("the server is closing");
            osw.flush();
            osw.close();
            response.flush();
            response.close();

            httpExchange.getResponseHeaders().set("Content-Type", TextFormat.CONTENT_TYPE_004);
            httpExchange.getResponseHeaders().set("Content-Length", String.valueOf(response.size()));
            httpExchange.sendResponseHeaders(200, response.size());
            response.writeTo(httpExchange.getResponseBody());
            httpExchange.close();

            callback.close();
            closableHttpServer.stop();
        }
    }

    public interface CloseHttpServerCallback {
        void close() throws IOException;
    }
}
