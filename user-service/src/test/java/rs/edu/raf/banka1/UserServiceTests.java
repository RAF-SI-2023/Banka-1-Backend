package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.UserService;
import rs.edu.raf.banka1.services.UserServiceImpl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTests {
    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private EmailService emailService;
    private PermissionRepository permissionRepository;

    private User mockUser;
    private Permission mockPermission;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapper();
        emailService = mock(EmailService.class);
        permissionRepository = mock(PermissionRepository.class);
        userService = new UserServiceImpl(userRepository, userMapper, emailService, permissionRepository);
        this.mockPermission = new Permission();
        this.mockUser = new User();
    }

    @Test
    void createUser() {
        String email = "noreply.rafbanka1@gmail.com";
        String firstName = "asdf";
        String lastName = "asdf";
        String jmbg = "1234";
        String position = "asdf";
        String phoneNumber = "1234";
        boolean isActive = true;
        String password = "1234";
        String activationToken = UUID.randomUUID().toString();
        userService.createUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive, activationToken);

        verify(emailService, times(1)).sendActivationEmail(eq(email), any(), any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void activateAccount() {
        when(userRepository.findByActivationToken(any()))
                .thenReturn(Optional.of(mockUser));

        String token = "1234";
        String password = "1234";
        userService.activateAccount(token, password);
        verify(userRepository, times(1)).findByActivationToken(token);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void editUser() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(mockUser));
        when(permissionRepository.findByName(any()))
                .thenReturn(Optional.of(mockPermission));

        String email = "noreply.rafbanka1@gmail.com";
        String firstName = "asdf";
        String lastName = "asdf";
        String jmbg = "1234";
        String position = "asdf";
        String phoneNumber = "1234";
        boolean isActive = true;
        String password = "1234";
        String perm = "can_manage_users";
        Set<String> permissions = new HashSet<>();
        permissions.add(perm);
        userService.createUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive);

        isActive = false;
        userService.editUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive, permissions);

        verify(userRepository, times(2)).save(any());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
