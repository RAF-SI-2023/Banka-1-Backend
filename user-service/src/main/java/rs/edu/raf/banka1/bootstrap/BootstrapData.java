package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         PermissionRepository permissionRepository,
                         ForeignCurrencyAccountRepository foreignCurrencyAccountRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");

        seedPermissions();

        User user1 = new User();
        user1.setEmail("admin");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        user1.setPermissions(new HashSet<>(permissionRepository.findAll()));

        User client = new User();
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setFirstName("Client");
        client.setLastName("ClientPrezime");
        userRepository.save(user1);
        userRepository.save(client);

        ForeignCurrencyAccount account1 = createForeignCurrencyAccount(client, user1);

        userRepository.save(client);
        foreignCurrencyAccountRepository.save(account1);

        System.out.println("Data loaded!");
    }

    private static ForeignCurrencyAccount createForeignCurrencyAccount(User client, User user1) {
        ForeignCurrencyAccount account1 = new ForeignCurrencyAccount();
        String creationDate = "2024-03-18";
        String expirationDate = "2024-03-18";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDateCreation = LocalDate.parse(creationDate, formatter);
        LocalDate localDateExpiration = LocalDate.parse(expirationDate, formatter);
        int dateIntegerCreation = (int) localDateCreation.toEpochDay();
        int dateIntegerExpiration = (int) localDateExpiration.toEpochDay();

        account1.setOwnerId(client.getUserId());
        account1.setCreatedByAgentId(user1.getUserId());
        account1.setAccountNumber("ACC123456789");
        account1.setBalance(1000.0);
        account1.setAvailableBalance(900.0);
        account1.setCreationDate(dateIntegerCreation);
        account1.setExpirationDate(dateIntegerExpiration);
        account1.setCurrency("USD");
        account1.setAccountStatus("ACTIVE");
        account1.setSubtypeOfAccount("LICNI");
        account1.setAccountMaintenance(10.0);
        account1.setDefaultCurrency(true);
        return account1;
    }

    private void seedPermissions() {
        for(String s : Arrays.asList("addUser", "modifyUser", "deleteUser", "readUser")) {
            if(permissionRepository.findByName(s).isPresent()) {
                continue;
            }

            Permission permission = new Permission();
            permission.setName(s);
            permission.setDescription(s);
            permissionRepository.save(permission);
        }

    }

}
