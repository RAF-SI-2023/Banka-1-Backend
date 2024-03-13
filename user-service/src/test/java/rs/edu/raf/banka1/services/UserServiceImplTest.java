package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UserServiceImpl.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private EmailServiceImpl emailServiceBean;

    @InjectMocks
    @Autowired
    private UserServiceImpl userService;

    @InjectMocks
    @Autowired
    private EmailServiceImpl emailService;

    private User mockUser;
    private Permission mockPermission;

    @BeforeEach
    public void setUp() {
        this.mockPermission = new Permission();
        this.mockUser = new User();
    }

    @Test
    public void testLoadUserByUsernameNoAuthorities() {
        User user = new User();
        String email = "user1";
        user.setUserId(1L);
        user.setEmail(email);
        user.setPassword("password");

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails =userService.loadUserByUsername(email);
        assertEquals(userDetails.getUsername(),email);
        assertEquals(userDetails.getPassword(),user.getPassword());
        assertEquals(userDetails.getAuthorities().size(),0);

    }

    @Test
    public void testLoadUserByUsernameAuthorities() {
        User user = new User();
        String email = "user1";
        user.setUserId(1L);
        user.setEmail(email);
        user.setPassword("password");

        Permission permission1 = new Permission();
        Permission permission2 = new Permission();
        permission1.setName("test_permission");
        permission2.setName("test_permission2");
        Set<Permission> permissionSet = new HashSet<>();
        permissionSet.add(permission1);
        permissionSet.add(permission2);
        user.setPermissions(permissionSet);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails =userService.loadUserByUsername(email);
        assertEquals(userDetails.getUsername(),email);
        assertEquals(userDetails.getPassword(),user.getPassword());
        assertEquals(userDetails.getAuthorities().size(),2);

    }

    @Test
    public void testLoadUserByUsernameNoUser() {
        String email = "user1";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
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
