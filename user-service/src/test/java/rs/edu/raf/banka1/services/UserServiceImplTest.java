package rs.edu.raf.banka1.services;

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
import rs.edu.raf.banka1.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {UserServiceImpl.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @InjectMocks
    @Autowired
    private UserServiceImpl userService;

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

}
