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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.employee.CreateEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EditEmployeeDto;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.services.implementations.EmailServiceImpl;
import rs.edu.raf.banka1.services.implementations.EmployeeServiceImpl;

import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
@SpringBootTest(classes = {EmployeeServiceImpl.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {
    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private EmployeeMapper userMapper;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private EmailServiceImpl emailServiceBean;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PermissionMapper permissionMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    @Autowired
    private EmployeeServiceImpl userService;

    @InjectMocks
    @Autowired
    private EmailServiceImpl emailService;

    private Employee mockUser;
    private Permission mockPermission;

    @BeforeEach
    public void setUp() {
        this.mockPermission = new Permission();
        this.mockUser = new Employee();
    }

    @Test
    public void testLoadUserByUsernameNoAuthorities() {
        Employee user = new Employee();
        String email = "user1";
        user.setUserId(1L);
        user.setEmail(email);
        user.setPassword("password");

        when(employeeRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);
        assertEquals(userDetails.getUsername(), email);
        assertEquals(userDetails.getPassword(), user.getPassword());
        assertEquals(userDetails.getAuthorities().size(), 0);

    }

    @Test
    public void testLoadUserByUsernameAuthorities() {
        Employee user = new Employee();
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

        when(employeeRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);
        assertEquals(userDetails.getUsername(), email);
        assertEquals(userDetails.getPassword(), user.getPassword());
        assertEquals(userDetails.getAuthorities().size(), 2);

    }

    @Test
    public void testLoadUserByUsernameNoUser() {
        String email = "user1";
        when(employeeRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
    }

    @Test
    void createUser() {
        when(userMapper.createEmployeeDtoToEmployee(any()))
                .thenReturn(mockUser);
        CreateEmployeeDto createUserRequest = new CreateEmployeeDto();
        createUserRequest.setEmail("noreply.rafbanka1@gmail.com");
        createUserRequest.setFirstName("asdf");
        createUserRequest.setLastName("asdf");
        createUserRequest.setJmbg("1234");
        createUserRequest.setPosition(Constants.ADMIN);
        createUserRequest.setPhoneNumber("1234");
        createUserRequest.setActive(true);
        userService.createEmployee(createUserRequest);

        verify(emailService, times(1)).sendEmail(eq(createUserRequest.getEmail()), any(), any());
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void activateAccount() {
        when(employeeRepository.findByActivationToken(any()))
                .thenReturn(Optional.of(mockUser));

        String token = "1234";
        String password = "1234";
        userService.activateAccount(token, password);
        verify(employeeRepository, times(1)).findByActivationToken(token);
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void sendResetPasswordEmail() {
        String email = "1234";
        when(employeeRepository.findByEmail(any()))
                .thenReturn(Optional.of(mockUser));
        when(emailService.sendEmail(eq(email), any(), any()))
                .thenReturn(true);

        assertEquals(userService.sendResetPasswordEmail(email), true);
        verify(emailService, times(1)).sendEmail(eq(email), any(), any());
    }

    @Test
    void sendResetPasswordEmailUserNotFound() {
        String email = "1234";
        when(employeeRepository.findByEmail(any()))
                .thenReturn(Optional.empty());
        when(emailService.sendEmail(eq(email), any(), any()))
                .thenReturn(true);

        assertEquals(userService.sendResetPasswordEmail(email), false);
        verify(emailService, times(0)).sendEmail(eq(email), any(), any());
    }

    @Test
    void setNewPassword() {
        when(employeeRepository.findByResetPasswordToken(any()))
                .thenReturn(Optional.of(mockUser));

        String token = "1234";
        String password = "1234";
        userService.setNewPassword(token, password);
        verify(employeeRepository, times(1)).findByResetPasswordToken(token);
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void setNewPasswordUserNotFound() {
        when(employeeRepository.findByResetPasswordToken(any())).thenReturn(Optional.empty());

        String token = "1234";
        String password = "1234";
        userService.setNewPassword(token, password);
        verify(employeeRepository, times(1)).findByResetPasswordToken(token);
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void editUser() {
        when(employeeRepository.findByEmail(any()))
                .thenReturn(Optional.of(mockUser));
        when(permissionRepository.findByName(any()))
                .thenReturn(Optional.of(mockPermission));
        when(userMapper.createEmployeeDtoToEmployee(any()))
                .thenReturn(mockUser);
        when(userMapper.editEmployeeDtoToEmployee(any(), any()))
                .thenReturn(mockUser);

        CreateEmployeeDto createUserRequest = new CreateEmployeeDto();
        createUserRequest.setEmail("noreply.rafbanka1@gmail.com");
        createUserRequest.setFirstName("asdf");
        createUserRequest.setLastName("asdf");
        createUserRequest.setJmbg("1234");
        createUserRequest.setPosition(Constants.ADMIN);
        createUserRequest.setPhoneNumber("1234");
        createUserRequest.setActive(true);
        userService.createEmployee(createUserRequest);

        EditEmployeeDto editUserRequest = new EditEmployeeDto();
        editUserRequest.setEmail("noreply.rafbanka1@gmail.com");
        editUserRequest.setFirstName("asdf");
        editUserRequest.setLastName("asdf");
//        editUserRequest.setJmbg("1234");
        editUserRequest.setPosition(Constants.ADMIN);
        editUserRequest.setPhoneNumber("1234");
        editUserRequest.setIsActive(false);
        String perm = "can_manage_users";
        Set<String> permissions = new HashSet<>();
        permissions.add(perm);
        editUserRequest.setPermissions(permissions.stream().toList());
        userService.editEmployee(editUserRequest);

        verify(employeeRepository, times(2)).save(any());
        verify(employeeRepository, times(1)).findByEmail(editUserRequest.getEmail());
    }

}
