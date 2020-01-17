package agent;

import io.prometheus.client.hotspot.DefaultExports;
import jmx.exporter.agent.httpserver.ClosableHttpServer;

import java.io.IOException;

public class MetricsServerTest {

    public static void main(String[] args) throws IOException {
        DefaultExports.initialize();
        new ClosableHttpServer(1234, new ClosableHttpServer.CloseHttpServerCallback() {
            @Override
            public void close() throws IOException {

            }
        });
    }

}
