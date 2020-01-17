package agent.launcher;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import lombok.extern.slf4j.Slf4j;
import sun.jvmstat.monitor.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class Main {

    private static VirtualMachine vm;
    private static String currentPid;

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, MonitorException, URISyntaxException {
        Scanner scanner = new Scanner(System.in);

        list();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::detach));

        while (true) {
            String pid = scanner.nextLine();
            if (pid == null || pid.equals("")) {
                detach();
                list();
            } else if ("shutdown".equals(pid.trim())) {
                detach();
                log.info("shutdown...");
                return;
            } else {
                log.info("attach ==> {}", pid);
                currentPid = pid;
                process(pid);
            }
        }

    }

    private static void list() throws MonitorException, URISyntaxException {
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        Set<?> vmList = new HashSet<Object>(local.activeVms());
        for(Object process : vmList) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
            String processName = MonitoredVmUtil.mainClass(vm, true);
            System.out.println(process + "\t" + processName);
        }
        System.out.println("--------------------------");
        System.out.println("input pid: ");
    }

    private static void process(String pid) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        vm = VirtualMachine.attach(pid);
        System.out.println("current path: " + System.getProperty("user.dir"));
        vm.loadAgent(System.getProperty("user.dir") + "/jmx-exporter-agent-jar-with-dependencies.jar");
    }

    private static void detach() {
        if (vm != null) {
            try {
                vm.detach();
                log.info("agent detach {}", currentPid);
            } catch (IOException e) {
                log.error("detach error", e);
            }
        }
    }
}
