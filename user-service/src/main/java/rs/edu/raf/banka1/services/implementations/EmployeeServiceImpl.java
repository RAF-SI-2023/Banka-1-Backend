package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import rs.edu.raf.banka1.responses.ActivateAccountResponse;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.responses.NewPasswordResponse;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.EmployeeService;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Value("${front.port}")
    private String frontPort;
    private EmployeeMapper employeeMapper;
    private PermissionMapper permissionMapper;
    private LimitMapper limitMapper;
    private EmployeeRepository employeeRepository;
    private PermissionRepository permissionRepository;
    private EmailService emailService;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper,
                               PermissionMapper permissionMapper,
                               EmployeeRepository employeeRepository,
                               PermissionRepository permissionRepository,
                               EmailService emailService,
                               JwtUtil jwtUtil,
                               PasswordEncoder passwordEncoder,
                               LimitMapper limitMapper){
        this.employeeMapper = employeeMapper;
        this.permissionMapper = permissionMapper;
        this.employeeRepository = employeeRepository;
        this.permissionRepository = permissionRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.limitMapper = limitMapper;
    }

    @Override
    public EmployeeDto findByEmail(String email) {
        return this.employeeRepository.findByEmail(email)
                .map(this.employeeMapper::employeeToEmployeeDto)
                .orElse(null);
    }

    @Override
    public List<EmployeeDto> findAll() {
        return this.employeeRepository.findAll()
                .stream()
                .map(this.employeeMapper::employeeToEmployeeDto)
                .toList();
    }

    @Override
    public EmployeeDto findById(Long id) {
        return this.employeeRepository.findById(id)
                .map(this.employeeMapper::employeeToEmployeeDto)
                .orElse(null);
    }

    @Override
    public EmployeeDto findByJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null)
            return null;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return findByEmail(userDetails.getUsername());
    }

    @Override
    public List<EmployeeDto> search(String email, String firstName, String lastName, String position) {
        return this.employeeRepository
                .searchUsersByEmailAndFirstNameAndLastNameAndPosition(email, firstName, lastName, position)
                .map(users -> users.stream().map(this.employeeMapper::employeeToEmployeeDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public CreateUserResponse createEmployee(CreateEmployeeDto createEmployeeDto) {
        if(!this.userHasValidPosition(createEmployeeDto))
            return new CreateUserResponse(null, "Employee doesn't have valid position");

        Employee employee = this.employeeMapper.createEmployeeDtoToEmployee(createEmployeeDto);

        String activationToken = UUID.randomUUID().toString();
        employee.setActivationToken(activationToken);

        Employee savedEmployee = this.employeeRepository.save(employee);
        emailService.sendEmail(createEmployeeDto.getEmail(), "RAF Banka - User activation",
                "Visit this URL to activate your account: http://localhost:" + this.frontPort + "/employee/set-password/" + activationToken);

        return new CreateUserResponse(employee.getUserId(), "Employee created successfully");
    }

    @Override
    public ActivateAccountResponse activateAccount(String token, String password) {
        Optional<Employee> optionalEmployee = employeeRepository.findByActivationToken(token);
        if (optionalEmployee.isEmpty()) return new ActivateAccountResponse(null);
        Employee employee = optionalEmployee.get();
        employee.setActivationToken(null);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setActive(true);
        employeeRepository.save(employee);

        return new ActivateAccountResponse(employee.getUserId());
    }

    @Override
    public boolean editEmployee(EditEmployeeDto editEmployeeDto) {
        Optional<Employee> employee = this.employeeRepository.findByEmail(editEmployeeDto.getEmail());

        if (employee.isEmpty())
            return false;

        Employee newEmployee = this.employeeMapper.editEmployeeDtoToEmployee(employee.get(), editEmployeeDto);
        this.employeeRepository.save(newEmployee);

        return true;
    }

    @Override
    public Boolean deleteEmployee(Long id) {
        Optional<Employee> employee = this.employeeRepository.findById(id);

        if(employee.isEmpty())
            return false;

        if(employee.get().getActive()){
            this.employeeRepository.deactivateUser(employee.get().getUserId());
            return true;
        }

        return false;
    }

    @Override
    public List<PermissionDto> findPermissions(Long userId) {
        Employee employee = this.employeeRepository.findById(userId).orElse(null);

        return employee == null ? new ArrayList<PermissionDto>() : extractPermissionsFromEmployee(employee);
    }

    @Override
    public List<PermissionDto> findPermissions(String email) {
        Employee employee = this.employeeRepository.findByEmail(email).orElse(null);

        return employee == null ? new ArrayList<PermissionDto>() : extractPermissionsFromEmployee(employee);
    }

    @Override
    public Boolean modifyEmployeePermissions(ModifyPermissionsRequest request, Long userId) {
        Employee employee = this.employeeRepository.findById(userId).orElse(null);

        if(employee == null)
            return false;

        Set<Permission> permissions = employee.getPermissions();

        for(String permissionName : request.getPermissions()){
            Optional<Permission> permission = this.permissionRepository.findByName(permissionName);
            if(request.getAdd()){
                permissions.add(permission.orElse(null));
            }else{
                permissions.remove(permission.orElse(null));
            }
        }

        this.employeeRepository.save(employee);

        return true;
    }

    @Override
    public Boolean sendResetPasswordEmail(String email) {
        Optional<Employee> optionalEmployee = this.employeeRepository.findByEmail(email);

        if (optionalEmployee.isEmpty()) return false;

        Employee employee = optionalEmployee.get();
        String resetPasswordToken = UUID.randomUUID().toString();

        employee.setResetPasswordToken(resetPasswordToken);
        this.employeeRepository.save(employee);

        return this.emailService.sendEmail(email, "RAF Banka - Password reset",
                "Visit this URL to reset your password: http://localhost:" + frontPort + "/employee/reset-password/" + resetPasswordToken);
    }

    @Override
    public NewPasswordResponse setNewPassword(String token, String password) {
        Optional<Employee> optionalEmployee = this.employeeRepository.findByResetPasswordToken(token);

        if (optionalEmployee.isEmpty()) {
            return new NewPasswordResponse();
        }

        Employee employee = optionalEmployee.get();

        employee.setResetPasswordToken(null);
        employee.setPassword(passwordEncoder.encode(password));
        this.employeeRepository.save(employee);

        return new NewPasswordResponse(employee.getUserId());
    }

    @Override
    public void resetLimitForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        employee.setLimitNow(0.0);
        employeeRepository.save(employee);
    }

    @Override
    public void resetEmployeeLimits() {
        List<Employee> users = employeeRepository.findAll();
        users.forEach(user->user.setLimitNow(0.0));
        employeeRepository.saveAll(users);
    }

    @Override
    public LimitDto setOrderLimitForEmployee(NewLimitDto newLimitDto) {
        Long employeeId = newLimitDto.getUserId();
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()-> new EmployeeNotFoundException(employeeId));
        if(!employee.getPosition().equalsIgnoreCase(Constants.AGENT)) {
            throw new ForbiddenException("Employee with id: " + employeeId + " is not in agent position. Changing the limit is prohibited.");
        }
        employee.setOrderlimit(newLimitDto.getLimit());
        employee.setRequireApproval(newLimitDto.getApprovalRequired());
        Employee saved = employeeRepository.save(employee);
        return limitMapper.toLimitDto(employee);
    }

    @Override
    public List<LimitDto> getAllLimits() {
        return this.employeeRepository.findAll().stream()
                .filter(employee -> employee.getPosition().equals(Constants.AGENT))
                .map(limitMapper::toLimitDto).collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeEntityByEmail(String email) {
        return this.employeeRepository.findByEmail(email).orElseThrow(ForbiddenException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> myEmployee = this.employeeRepository.findByEmail(username);

        if (myEmployee.isEmpty()) {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }

        Employee employee = myEmployee.get();

        List<SimpleGrantedAuthority> authorities = employee.getPermissions()
                .stream()
                .map((permission -> new SimpleGrantedAuthority(permission.getName())))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(employee.getEmail(),
                employee.getPassword(),
                authorities);
    }

//    private Employee updateFields(Employee dbEmployee, Employee editEmployee){
//        if(editEmployee.getPassword() != null)
//            dbEmployee.setPassword(this.passwordEncoder.encode(editEmployee.getPassword()));
//
//        if(editEmployee.getFirstName() != null)
//            dbEmployee.setFirstName(editEmployee.getFirstName());
//
//        if(editEmployee.getLastName() != null)
//            dbEmployee.setLastName(editEmployee.getLastName());
//
//        if(editEmployee.getPhoneNumber() != null)
//            dbEmployee.setPhoneNumber(editEmployee.getPhoneNumber());
//
//        if(editEmployee.getActive() != null)
//            dbEmployee.setActive(editEmployee.getActive());
//
//        if(editEmployee.getPosition() != null)
//            dbEmployee.setPosition(editEmployee.getPosition());
//
//        if(editEmployee.getPermissions() != null){
//           dbEmployee.setPermissions(editEmployee.getPermissions());
//        }
//
//        return dbEmployee;
//    }

    private List<PermissionDto> extractPermissionsFromEmployee(Employee employee) {
        return employee.getPermissions()
                .stream()
                .map(this.permissionMapper::permissionToPermissionDto)
                .collect(Collectors.toList());
    }

    private boolean userHasValidPosition(CreateEmployeeDto createEmployeeDto) {
        return (createEmployeeDto.getPosition().equalsIgnoreCase(Constants.AGENT) ||
                createEmployeeDto.getPosition().equalsIgnoreCase(Constants.SUPERVIZOR) ||
                createEmployeeDto.getPosition().equalsIgnoreCase(Constants.ADMIN));
    }
}
