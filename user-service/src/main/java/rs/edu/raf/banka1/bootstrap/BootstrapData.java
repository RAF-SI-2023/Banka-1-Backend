package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;

import java.util.*;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyRepository currencyRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public BootstrapData(
        final UserRepository userRepository,
        final PasswordEncoder passwordEncoder,
        final PermissionRepository permissionRepository,
        final CurrencyRepository currencyRepository,
        final CustomerRepository customerRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.currencyRepository = currencyRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");

        seedPermissions();
        seedUsers();
        seedCustomers();
        seedCurrencies();

        System.out.println("Data loaded!");
    }

    private void seedPermissions() {
        for(String s : Arrays.asList(
            "addUser", "modifyUser", "deleteUser", "readUser",
            "modifyCustomer", "manage_loans", "manage_loan_requests")
        ) {
            if(permissionRepository.findByName(s).isPresent()) {
                continue;
            }

            Permission permission = new Permission();
            permission.setName(s);
            permission.setDescription(s);
            permissionRepository.save(permission);
        }
    }

    private void seedUsers() {
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

        userRepository.save(client);
    }

    private void seedCustomers() {
        Customer customer = new Customer();
        customer.setEmail("customer@gmail.com");
        customer.setPassword(passwordEncoder.encode("customer"));
        customer.setFirstName("CustomerName");
        customer.setLastName("CustomerLastName");
        customerRepository.save(customer);
    }

    private void seedCurrencies() {
        //loading currencies
        Set<Currency> currencies = Currency.getAvailableCurrencies();
        for(Currency currency : currencies) {
            if(currencyRepository.findCurrencyByCurrencyCode(currency.getCurrencyCode()).isPresent()) {
                continue;
            }
            rs.edu.raf.banka1.model.Currency myCurrency = new rs.edu.raf.banka1.model.Currency();
            myCurrency.setCurrencyName(currency.getDisplayName());
            myCurrency.setCurrencyCode(currency.getCurrencyCode());
            myCurrency.setCurrencySymbol(currency.getSymbol());
            myCurrency.setActive(true);

            Locale locale = new Locale("", currency.getCurrencyCode());
            String country = locale.getDisplayCountry();

            myCurrency.setCountry(country);

            currencyRepository.save(myCurrency);
        }
    }
}
