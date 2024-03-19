package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("Loading Data...");
//
//        Permission testPermission = new Permission();
//        testPermission.setName("can_manage_users");
//        permissionRepository.save(testPermission);
//        Set<Permission> permissions1 = new HashSet<>();
//        permissions1.add(testPermission);
//
//        User user1 = new User();
//        user1.setEmail("user1@gmail.com");
//        user1.setPassword(passwordEncoder.encode("user1"));
//        user1.setFirstName("User1");
//        user1.setLastName("User1Prezime");
//        user1.setPermissions(permissions1);
//        userRepository.save(user1);
//
//        System.out.println("Data loaded!");
    }
}
