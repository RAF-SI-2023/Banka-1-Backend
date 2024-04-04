package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
//import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.Employee;
//import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
//import rs.edu.raf.banka1.repositories.UserRepository;
//import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.EmployeeService;
import rs.edu.raf.banka1.services.implementations.EmployeeServiceImpl;
//import rs.edu.raf.banka1.services.UserService;
//import rs.edu.raf.banka1.services.UserServiceImpl;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SearchAndFilterTests {
//    private UserService userService;
//    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
//    private UserMapper userMapper;
    private EmployeeMapper employeeMapper;
    private EmployeeService employeeService;
    private EmailService emailService;
    private PermissionRepository permissionRepository;
    private PermissionMapper permissionMapper;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    private Employee admin;
    private Employee user1;
    private Employee user2;

    @BeforeEach
    public void setUp() {
//        userRepository = mock(UserRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        emailService = mock(EmailService.class);
        jwtUtil = mock(JwtUtil.class);
        permissionRepository = mock(PermissionRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        employeeMapper = new EmployeeMapper(new PermissionMapper(), passwordEncoder, permissionRepository);


//        userService = new UserServiceImpl(userRepository, userMapper, emailService, permissionRepository, jwtUtil,
//                permissionMapper, passwordEncoder);

//        EmployeeServiceImpl(EmployeeMapper employeeMapper,
//                PermissionMapper permissionMapper,
//                EmployeeRepository employeeRepository,
//                PermissionRepository permissionRepository,
//                EmailService emailService,
//                JwtUtil jwtUtil,
//                PasswordEncoder passwordEncoder)

        employeeService = new EmployeeServiceImpl(employeeMapper, permissionMapper, employeeRepository,
                permissionRepository, emailService, jwtUtil, passwordEncoder);

        this.admin = new Employee();
        admin.setActive(true);
        admin.setJmbg("000000000");
        admin.setEmail("admin@gmail.com");
//        admin.setPosition("admin");
        admin.setPassword("admin");
        admin.setFirstName("admin");
        admin.setLastName("adminic");


        this.user1 = new Employee();
        user1.setActive(true);
        user1.setJmbg("123456789");
        user1.setEmail("user1@gmail.com");
//        user1.setPosition("position1");
        user1.setPassword("1234");
        user1.setFirstName("user1");
        user1.setLastName("useric1");


        this.user2 = new Employee();
        user2.setActive(true);
        user2.setJmbg("987654321");
        user2.setEmail("user2@gmail.com");
//        user2.setPosition("position2");
        user2.setPassword("4321");
        user2.setFirstName("user2");
        user2.setLastName("useric2");
    }

    @Test
    void noParametersNull() {
        // Mock the userRepository to return no user data
        when(employeeRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());

        List<EmployeeDto> userResponses = employeeService.search(null, null, null, null);
        assertEquals(0, userResponses.size());
    }

    @Test
    void noParametersEmptyString() {
        final Integer testCount = 3;
        // Mock the userRepository to return no user data
        when(employeeRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                        this.admin,
                        this.user1,
                        this.user2
                )));

        List<EmployeeDto> userResponses = employeeService.search("", "", "", "");
        assertEquals(testCount, userResponses.size());
    }

    @Test
    void allParametersOneOutput() {
        final Integer testCount = 1;
        when(employeeRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                        this.user1
                )));

        List<EmployeeDto> userResponses = employeeService.search("user", "user", "useric1", "position1");
        assertEquals(testCount, userResponses.size());

    }

    @Test
    void allParametersNoOutput() {
        when(employeeRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                )));

        List<EmployeeDto> userResponses = employeeService.search("admin", "user", "useric1", "position1");
        assertEquals(0, userResponses.size());

    }
    @Test
    void allParametersTwoOutputs() {
        final Integer testCount = 2;
        when(employeeRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(Arrays.asList(
                        this.user1,
                        this.user2
                )));

        List<EmployeeDto> userResponses = employeeService.search("user", "user", "useric", "position");
        assertEquals(testCount, userResponses.size());

    }



}


