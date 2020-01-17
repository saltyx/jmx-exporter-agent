package jmx.exporter.agent;

import io.prometheus.client.hotspot.DefaultExports;
import jmx.exporter.agent.httpserver.ClosableHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

@Slf4j
public class AgentMain {

    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws IOException {
        DefaultExports.initialize();
        final ClosableHttpServer httpServer = new ClosableHttpServer(1234, () -> System.out.println("agent exiting..."));

        Runtime.getRuntime().addShutdownHook(new Thread(httpServer::stop));
    }

}
