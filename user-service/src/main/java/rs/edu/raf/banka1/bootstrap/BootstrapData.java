package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.services.UserService;

import java.util.HashSet;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        User user1 = new User();
        user1.setEmail("user1@gmail.com");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        user1.setPermissions(new HashSet<>());
        userRepository.save(user1);

        System.out.println("Data loaded!");
    }
}
