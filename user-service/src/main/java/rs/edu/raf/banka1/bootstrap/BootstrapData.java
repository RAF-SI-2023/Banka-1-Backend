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
        System.out.println("Loading Data...");

        Permission readPermission = new Permission();
        readPermission.setName("readUser");
        Permission addPermission = new Permission();
        addPermission.setName("addUser");
        Permission modifyPermission = new Permission();
        modifyPermission.setName("modifyUser");
        Permission deletePermission = new Permission();
        deletePermission.setName("deleteUser");

        permissionRepository.save(readPermission);
        permissionRepository.save(addPermission);
        permissionRepository.save(modifyPermission);
        permissionRepository.save(deletePermission);

        Set<Permission> adminPermissions = new HashSet<>();
        adminPermissions.add(readPermission);
        adminPermissions.add(addPermission);
        adminPermissions.add(modifyPermission);
        adminPermissions.add(deletePermission);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setFirstName("admin");
        admin.setLastName("admin");
        admin.setPermissions(adminPermissions);
        userRepository.save(admin);

        User user1 = new User();
        user1.setEmail("user1@gmail.com");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        userRepository.save(user1);

        System.out.println("Data loaded!");
    }
}
