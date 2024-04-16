package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.LimitDto;
import rs.edu.raf.banka1.dtos.NewLimitDto;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.dtos.employee.CreateEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EditEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.exceptions.EmployeeNotFoundException;
import rs.edu.raf.banka1.exceptions.ForbiddenException;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.LimitMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.requests.ModifyPermissionsRequest;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.services.implementations.EmailServiceImpl;
import rs.edu.raf.banka1.services.implementations.EmployeeServiceImpl;

import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

//@SpringBootTest(classes = {EmployeeServiceImpl.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeMapper userMapper;
//    @Mock
    private LimitMapper limitMapper;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private EmailServiceImpl emailServiceBean;

    @Mock
    private JwtUtil jwtUtil;

    private PermissionMapper permissionMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeServiceImpl employeeService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    @Spy
    private EmailServiceImpl emailService;

    private Employee mockUser;
    private Permission mockPermission;

    private Employee admin;
    private Employee user1;
    private Employee user2;

    @BeforeEach
    public void setUp(){
        this.mockPermission = new Permission();
        this.mockUser = new Employee();
        this.permissionMapper = new PermissionMapper();
        this.userMapper = new EmployeeMapper(this.permissionMapper,
                this.passwordEncoder, this.permissionRepository);
        this.limitMapper = new LimitMapper();

        this.employeeService = new EmployeeServiceImpl(this.userMapper,
                this.permissionMapper, this.employeeRepository, this.permissionRepository, this.emailService, this.jwtUtil, this.passwordEncoder,
                this.limitMapper);

        this.admin = new Employee();
        admin.setActive(true);
        admin.setJmbg("000000000");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("admin");
        admin.setFirstName("admin");
        admin.setLastName("adminic");

        this.user1 = new Employee();
        user1.setActive(true);
        user1.setJmbg("123456789");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setFirstName("user1");
        user1.setLastName("useric1");

        this.user2 = new Employee();
        user2.setActive(true);
        user2.setJmbg("987654321");
        user2.setEmail("user2@gmail.com");
        user2.setPassword("4321");
        user2.setFirstName("user2");
        user2.setLastName("useric2");
    }

    @Test
    public void testLoadUserByUsernameNoAuthorities() {
        Employee user = new Employee();
        String email = "user1";
        user.setUserId(1L);
        user.setEmail(email);
        user.setPassword("password");

        when(employeeRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = employeeService.loadUserByUsername(email);
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

        UserDetails userDetails = employeeService.loadUserByUsername(email);
        assertEquals(userDetails.getUsername(), email);
        assertEquals(userDetails.getPassword(), user.getPassword());
        assertEquals(userDetails.getAuthorities().size(), 2);

    }

    @Test
    public void testLoadUserByUsernameNoUser() {
        String email = "user1";
        when(employeeRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            employeeService.loadUserByUsername(email);
        });
    }

    @Test
    void createUser() {
//        when(userMapper.createEmployeeDtoToEmployee(any()))
//                .thenReturn(mockUser);
        CreateEmployeeDto createUserRequest = new CreateEmployeeDto();
        createUserRequest.setEmail("noreply.rafbanka1@gmail.com");
        createUserRequest.setFirstName("asdf");
        createUserRequest.setLastName("asdf");
        createUserRequest.setJmbg("1234");
        createUserRequest.setPosition(Constants.ADMIN);
        createUserRequest.setPhoneNumber("1234");
        createUserRequest.setActive(true);
        employeeService.createEmployee(createUserRequest);

        verify(emailService, times(1)).sendEmail(eq(createUserRequest.getEmail()), any(), any());
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void activateAccount() {
        when(employeeRepository.findByActivationToken(any()))
                .thenReturn(Optional.of(mockUser));

        String token = "1234";
        String password = "1234";
        employeeService.activateAccount(token, password);
        verify(employeeRepository, times(1)).findByActivationToken(token);
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    public void activateAccountEmployeeNotFound(){
        when(employeeRepository.findByActivationToken("testactivationtoken")).thenReturn(Optional.empty());
        var result = employeeService.activateAccount("testactivationtoken", "password");
        assertNull(result.getUserId());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void sendResetPasswordEmail() {
        String email = "1234";
        when(employeeRepository.findByEmail(any()))
                .thenReturn(Optional.of(mockUser));
        when(emailService.sendEmail(eq(email), any(), any()))
                .thenReturn(true);

        assertEquals(employeeService.sendResetPasswordEmail(email), true);
        verify(emailService, times(1)).sendEmail(eq(email), any(), any());
    }

    @Test
    void sendResetPasswordEmailUserNotFound() {
        String email = "1234";
        when(employeeRepository.findByEmail(any()))
                .thenReturn(Optional.empty());
        when(emailService.sendEmail(eq(email), any(), any()))
                .thenReturn(true);

        assertEquals(employeeService.sendResetPasswordEmail(email), false);
        verify(emailService, times(0)).sendEmail(eq(email), any(), any());
    }

    @Test
    void setNewPassword() {
        when(employeeRepository.findByResetPasswordToken(any()))
                .thenReturn(Optional.of(mockUser));

        String token = "1234";
        String password = "1234";
        employeeService.setNewPassword(token, password);
        verify(employeeRepository, times(1)).findByResetPasswordToken(token);
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void setNewPasswordUserNotFound() {
        when(employeeRepository.findByResetPasswordToken(any())).thenReturn(Optional.empty());

        String token = "1234";
        String password = "1234";
        employeeService.setNewPassword(token, password);
        verify(employeeRepository, times(1)).findByResetPasswordToken(token);
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void editUser() {
        when(employeeRepository.findByEmail(any()))
                .thenReturn(Optional.of(mockUser));
        when(permissionRepository.findByName(any()))
                .thenReturn(Optional.of(mockPermission));

        CreateEmployeeDto createUserRequest = new CreateEmployeeDto();
        createUserRequest.setEmail("noreply.rafbanka1@gmail.com");
        createUserRequest.setFirstName("asdf");
        createUserRequest.setLastName("asdf");
        createUserRequest.setJmbg("1234");
        createUserRequest.setPosition(Constants.ADMIN);
        createUserRequest.setPhoneNumber("1234");
        createUserRequest.setActive(true);
        employeeService.createEmployee(createUserRequest);

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
        employeeService.editEmployee(editUserRequest);

        verify(employeeRepository, times(2)).save(any());
        verify(employeeRepository, times(1)).findByEmail(editUserRequest.getEmail());
    }

    @Test
    void noParametersNull() {
        when(employeeRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        List<EmployeeDto> userResponses = employeeService.search(null, null, null, null);
        assertEquals(0, userResponses.size());
    }

    @Test
    void noParametersEmptyString() {
        final Integer testCount = 3;
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

    @Test
    void setOrderLimitForEmployeeTest() {
        NewLimitDto newLimitDto = new NewLimitDto();
        newLimitDto.setUserId(1L);
        newLimitDto.setLimit(10005.0);
        newLimitDto.setApprovalRequired(true);

        Employee employee = new Employee();
        employee.setUserId(1L);
        employee.setPosition(Constants.AGENT);
        employee.setEmail("email");
        employee.setLimitNow(560.0);
        employee.setRequireApproval(true);

        LimitDto expected = new LimitDto();
        expected.setLimit(10005.0);
        expected.setEmail("email");
        expected.setApprovalRequired(true);
        expected.setUsedLimit(560.0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        LimitDto actualLimitDto = this.employeeService.setOrderLimitForEmployee(newLimitDto);

        assertEquals(actualLimitDto.getLimit(), 10005.0);
        assertEquals(actualLimitDto.getEmail(), "email");
        assertEquals(actualLimitDto.getApprovalRequired(), true);
        assertEquals(actualLimitDto.getUsedLimit(), 560.0);
    }
    @Test
    void setOrderLimitForEmployee_Exception_Test() {
        NewLimitDto newLimitDto = new NewLimitDto();
        newLimitDto.setUserId(1L);
        newLimitDto.setLimit(10005.0);
        newLimitDto.setApprovalRequired(true);

        Employee employee = new Employee();
        employee.setUserId(1L);
        employee.setPosition(Constants.SUPERVIZOR);
        employee.setEmail("email");
        employee.setLimitNow(560.0);
        employee.setRequireApproval(true);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(ForbiddenException.class, () -> this.employeeService.setOrderLimitForEmployee(newLimitDto));
    }

    @Test
    public void testResetEmployeeLimits() {
        List<Employee> mockEmployees = new ArrayList<>();
        Employee e1 = new Employee();
        e1.setLimitNow(1000.0);
        Employee e2 = new Employee();
        e2.setLimitNow(2000.0);
        mockEmployees.add(e1);
        mockEmployees.add(e2);

        when(employeeRepository.findAll()).thenReturn(mockEmployees);

        employeeService.resetEmployeeLimits();

        for (Employee employee : mockEmployees) {
            assertEquals(0.0, employee.getLimitNow());
        }

        verify(employeeRepository, times(1)).saveAll(mockEmployees);
    }

    @Test
    public void testResetLimitForEmployee() {
        Long employeeId = 1L;
        Employee mockEmployee = new Employee();
        mockEmployee.setLimitNow(1000.0);
        mockEmployee.setUserId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mockEmployee));

        employeeService.resetLimitForEmployee(employeeId);

        assertEquals(0.0, mockEmployee.getLimitNow());

        verify(employeeRepository, times(1)).save(mockEmployee);
    }

    @Test
    public void testResetLimitForEmployee_EmployeeNotFound() {
        Long employeeId = 1L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.resetLimitForEmployee(employeeId);
        });

        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void testGetAllLimits() {
        user1.setPosition(Constants.AGENT);
        user1.setOrderlimit(1000.0);
        user1.setLimitNow(450.0);

        user2.setPosition(Constants.AGENT);
        user2.setOrderlimit(1050.0);
        user2.setLimitNow(400.0);

        admin.setPosition(Constants.ADMIN);

        LimitDto limitDto1 = new LimitDto();
        limitDto1.setLimit(1000.0);
        limitDto1.setUsedLimit(450.0);
        limitDto1.setEmail("user1@gmail.com");
        limitDto1.setApprovalRequired(true);

        LimitDto limitDto2 = new LimitDto();
        limitDto2.setLimit(1050.0);
        limitDto2.setUsedLimit(400.0);
        limitDto2.setEmail("user2@gmail.com");
        limitDto2.setApprovalRequired(true);

        List<Employee> employees = Arrays.asList(user1, user2);
        List<LimitDto> expectedLimits = Arrays.asList(limitDto1, limitDto2);

        when(employeeRepository.findAll()).thenReturn(employees);

        List<LimitDto> actualLimits = employeeService.getAllLimits();

        assertEquals(expectedLimits.size(), actualLimits.size());
    }

    @Test
    public void testGetEmployeeEntityByEmail_EmployeeFound() {
        String email = "user1@gmail.com";
        Employee expectedEmployee = new Employee();
        expectedEmployee.setEmail("user1@gmail.com");
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(expectedEmployee));
        Employee actualEmployee = employeeService.getEmployeeEntityByEmail(email);
        assertEquals(user1.getEmail(), actualEmployee.getEmail());
    }

    @Test
    public void testGetEmployeeEntityByEmail_EmployeeNotFound() {
        String email = "nonexistent@example.com";
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(ForbiddenException.class, () -> employeeService.getEmployeeEntityByEmail(email));
    }
    public void editEmployeePermissionEmployeeNotFound(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        var result = employeeService.modifyEmployeePermissions(new ModifyPermissionsRequest(), 1L);

        assertThat(result).isFalse();

        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void editEmployeePermissionAddPermisson(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(user1));
        ModifyPermissionsRequest modifyPermissionsRequest = new ModifyPermissionsRequest();
        modifyPermissionsRequest.setPermissions(new ArrayList<>(Arrays.asList("can_manage_users")));
        modifyPermissionsRequest.setAdd(true);
        mockPermission.setName("can_manage_users");
        when(permissionRepository.findByName("can_manage_users")).thenReturn(Optional.of(mockPermission));

        var result = employeeService.modifyEmployeePermissions(modifyPermissionsRequest, 1L);

        assertThat(result).isTrue();

        verify(employeeRepository, times(1)).save(user1);
    }

    @Test
    public void editEmployeePermissionRemovePermisson(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(user1));
        ModifyPermissionsRequest modifyPermissionsRequest = new ModifyPermissionsRequest();
        modifyPermissionsRequest.setPermissions(new ArrayList<>(Arrays.asList("can_manage_users")));
        modifyPermissionsRequest.setAdd(false);
        mockPermission.setName("can_manage_users");
        when(permissionRepository.findByName("can_manage_users")).thenReturn(Optional.of(mockPermission));

        var result = employeeService.modifyEmployeePermissions(modifyPermissionsRequest, 1L);

        assertThat(result).isTrue();

        verify(employeeRepository, times(1)).save(user1);
    }

    @Test
    public void deleteEmployeeNotFound(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        var result = employeeService.deleteEmployee(1L);
        assertThat(result).isFalse();

        verify(employeeRepository, never()).deactivateUser(any());
    }

    @Test
    public void deleteEmployeeSuccess(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(user1));
        user1.setUserId(1L);
        var result = employeeService.deleteEmployee(1L);
        assertThat(result).isTrue();

        verify(employeeRepository, times(1)).deactivateUser(1L);
    }

    @Test
    public void findPermissionsById(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(user1));
        mockPermission.setName("test");
        user1.getPermissions().add(mockPermission);
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setPermissionId(1L);
        permissionDto.setName("test");
        permissionDto.setDescription("test");
//        when(permissionMapper.permissionToPermissionDto(mockPermission)).thenReturn(permissionDto);

        List<PermissionDto> result = employeeService.findPermissions(1L);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("test");
    }

    @Test
    public void findPermissionsByString(){
        when(employeeRepository.findByEmail("test")).thenReturn(Optional.of(user1));
        mockPermission.setName("test");
        user1.getPermissions().add(mockPermission);
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setPermissionId(1L);
        permissionDto.setName("test");
        permissionDto.setDescription("test");
//        when(permissionMapper.permissionToPermissionDto(mockPermission)).thenReturn(permissionDto);

        List<PermissionDto> result = employeeService.findPermissions("test");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("test");
    }

    @Test
    public void findByJWTAuthNull(){
        try(MockedStatic<SecurityContextHolder> security = mockStatic(SecurityContextHolder.class)) {
            SecurityContext mycontext = mock(SecurityContext.class);
            when(SecurityContextHolder.getContext()).thenReturn(mycontext);
            when(mycontext.getAuthentication()).thenReturn(null);
            security.when(SecurityContextHolder::getContext).thenReturn(mycontext);

            var result = employeeService.findByJwt();

            assertThat(result).isNull();
        }
    }

    @Test
    public void findByJWTAuthNotNull(){
        try(MockedStatic<SecurityContextHolder> security = mockStatic(SecurityContextHolder.class)) {
            SecurityContext mycontext = mock(SecurityContext.class);
            when(SecurityContextHolder.getContext()).thenReturn(mycontext);
            Authentication myauth = mock(Authentication.class);
            when(mycontext.getAuthentication()).thenReturn(myauth);
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("test");
            when(employeeRepository.findByEmail("test")).thenReturn(Optional.of(user1));
            when(myauth.getPrincipal()).thenReturn(userDetails);
            when(mycontext.getAuthentication()).thenReturn(myauth);
            security.when(SecurityContextHolder::getContext).thenReturn(mycontext);
//            when(userMapper.employeeToEmployeeDto(user1)).thenReturn(new EmployeeDto());

            var result = employeeService.findByJwt();

            assertThat(result).isNotNull();
        }
    }

    @Test
    public void findById(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(user1));
//        when(userMapper.employeeToEmployeeDto(user1)).thenReturn(new EmployeeDto());

        var result = employeeService.findById(1L);

        assertThat(result).isNotNull();
    }

//    @Test
//    public void getAllLimits(){
//        List<Employee> employees = new ArrayList<>();
//        employees.add(user1);
//        employees.add(user2);
//        when(employeeRepository.findAll()).thenReturn(employees);
//        when(limitMapper.toLimitDto(user1)).thenReturn(new LimitDto());
//        when(limitMapper.toLimitDto(user2)).thenReturn(new LimitDto());
//
//        var result = employeeService.getAllLimits();
//
//        assertThat(result.size()).isEqualTo(2);
//    }

    @Test
    public void findAll(){
        List<Employee> employees = new ArrayList<>();
        employees.add(user1);
        employees.add(user2);
        when(employeeRepository.findAll()).thenReturn(employees);
//        when(userMapper.employeeToEmployeeDto(user1)).thenReturn(new EmployeeDto());
//        when(userMapper.employeeToEmployeeDto(user2)).thenReturn(new EmployeeDto());

        var result = employeeService.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void getEmployeeEntityById(){
        when(employeeRepository.findByEmail("test")).thenReturn(Optional.of(user1));
        var result = employeeService.getEmployeeEntityByEmail("test");
        assertThat(result).isNotNull();
    }

    @Test
    public void createEmployeeNotValidPosition(){
        CreateEmployeeDto createUserRequest = new CreateEmployeeDto();
        createUserRequest.setEmail("test");
        createUserRequest.setPosition("asdjkasdkasd");

        CreateUserResponse result = employeeService.createEmployee(createUserRequest);

        assertThat(result.getUserId()).isNull();
        verify(employeeRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), any(), any());
    }
}
