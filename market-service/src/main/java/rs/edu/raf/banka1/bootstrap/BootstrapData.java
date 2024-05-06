package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Logger.info("Loading Data...");
    }
}
