package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.UserService;
import rs.edu.raf.banka1.services.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SearchAndFilterTests {
    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private EmailService emailService;
    private PermissionRepository permissionRepository;

    private User admin;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapper();
        emailService = mock(EmailService.class);
        userService = new UserServiceImpl(userRepository, userMapper, emailService, permissionRepository);

        this.admin = new User();
        admin.setActive(true);
        admin.setJmbg("000000000");
        admin.setEmail("admin@gmail.com");
        admin.setPosition("admin");
        admin.setPassword("admin");
        admin.setFirstName("admin");
        admin.setLastName("adminic");


        this.user1 = new User();
        user1.setActive(true);
        user1.setJmbg("123456789");
        user1.setEmail("user1@gmail.com");
        user1.setPosition("position1");
        user1.setPassword("1234");
        user1.setFirstName("user1");
        user1.setLastName("useric1");


        this.user2 = new User();
        user2.setActive(true);
        user2.setJmbg("987654321");
        user2.setEmail("user2@gmail.com");
        user2.setPosition("position2");
        user2.setPassword("4321");
        user2.setFirstName("user2");
        user2.setLastName("useric2");
    }

    @Test
    void noParametersNull() {
        // Mock the userRepository to return no user data
        when(userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());

        List<UserResponse> userResponses = userService.search(null, null, null, null);
        assertEquals(0, userResponses.size());
    }

    @Test
    void noParametersEmptyString() {
        // Mock the userRepository to return no user data
        when(userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                        this.admin,
                        this.user1,
                        this.user2
                )));

        List<UserResponse> userResponses = userService.search("", "", "", "");
        assertEquals(3, userResponses.size());
    }

    @Test
    void allParametersOneOutput() {
        when(userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                        this.user1
                )));

        List<UserResponse> userResponses = userService.search("user", "user", "useric1", "position1");
        assertEquals(1, userResponses.size());

    }

    @Test
    void allParametersNoOutput() {
        when(userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                )));

        List<UserResponse> userResponses = userService.search("admin", "user", "useric1", "position1");
        assertEquals(0, userResponses.size());

    }
    @Test
    void allParametersTwoOutputs() {
        when(userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                        this.user1,
                        this.user2
                )));

        List<UserResponse> userResponses = userService.search("user", "user", "useric", "position");
        assertEquals(2, userResponses.size());

    }



}


