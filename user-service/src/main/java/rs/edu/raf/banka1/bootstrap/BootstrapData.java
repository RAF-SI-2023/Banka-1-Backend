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
    public BootstrapData(UserRepository userRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");


        Permission can_manage_users = new Permission();
        can_manage_users.setName("can_manage_users");
        can_manage_users.setDescription("admin");
        this.permissionRepository.save(can_manage_users);

        User admin = new User();
        admin.setActive(true);
        admin.setJmbg("0");
        admin.setEmail("admin@adminovic.com");
        admin.setPassword("admin");
        admin.setFirstName("Admin");
        admin.setLastName("Adminovic");
        admin.setPhoneNumber("0691337420");
        Set<Permission> adminPerms = new HashSet<>();
        adminPerms.add(can_manage_users);
        admin.setPermissions(adminPerms);
        admin.setPosition("admin");
        this.userRepository.save(admin);

        System.out.println("Data loaded!");
    }
}
