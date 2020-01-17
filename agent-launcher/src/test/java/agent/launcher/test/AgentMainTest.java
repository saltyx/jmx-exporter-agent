package agent.launcher.test;

import java.util.concurrent.TimeUnit;

public class AgentMainTest {

    private static Bean1 variable = new Bean1();

    public static void main(String[] args) throws InterruptedException {
        variable.setId("1111");
        while (true) {
            System.out.println("========> running " + variable.getId());
            TimeUnit.SECONDS.sleep(10);
        }
    }

}
