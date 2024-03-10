package rs.edu.raf.banka1.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        System.out.println("Data loaded!");
    }
}
