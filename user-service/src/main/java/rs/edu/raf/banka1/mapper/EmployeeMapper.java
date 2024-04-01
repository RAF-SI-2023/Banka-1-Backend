package rs.edu.raf.banka1.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.dtos.employee.CreateEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EditEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.repositories.PermissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {
    private PermissionMapper permissionMapper;
    private PasswordEncoder passwordEncoder;
    private PermissionRepository permissionRepository;

    public EmployeeMapper(PermissionMapper permissionMapper,
                          PasswordEncoder passwordEncoder,
                          PermissionRepository permissionRepository) {
        this.permissionMapper = permissionMapper;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
    }

    public EmployeeDto employeeToEmployeeDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setJmbg(employee.getJmbg());
        employeeDto.setPhoneNumber(employee.getPhoneNumber());
        employeeDto.setPosition(employee.getPosition());

        List<PermissionDto> permissionDtoList = new ArrayList<>();

        for (Permission permission : employee.getPermissions()) {
            permissionDtoList.add(permissionMapper.permissionToPermissionDto(permission));
        }

        employeeDto.setPermissions(permissionDtoList);

        return employeeDto;
    }

    public Employee createEmployeeDtoToEmployee(CreateEmployeeDto createEmployeeDto) {
        Employee employee = new Employee();
        employee.setEmail(createEmployeeDto.getEmail());
        employee.setFirstName(createEmployeeDto.getFirstName());
        employee.setLastName(createEmployeeDto.getLastName());
        employee.setJmbg(createEmployeeDto.getJmbg());
        employee.setPhoneNumber(createEmployeeDto.getPhoneNumber());
        employee.setActive(false);
        employee.setPassword(UUID.randomUUID().toString());

        return employee;
    }

    public Employee editEmployeeDtoToEmployee(EditEmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setEmail(employeeDto.getEmail());
        employee.setPassword(employeeDto.getPassword());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setPosition(employeeDto.getPosition());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setActive(employeeDto.getIsActive());
        employee.setPermissions(employeeDto.getPermissions()
                .stream()
                .map(permission -> permissionRepository.findByName(permission).orElseThrow())
                .collect(Collectors.toSet()));

        return employee;
    }

    public EditEmployeeDto employeeToEditEmployeeDto(Employee employee) {
        EditEmployeeDto editEmployeeDto = new EditEmployeeDto();
        editEmployeeDto.setEmail(employee.getEmail());
        editEmployeeDto.setFirstName(employee.getFirstName());
        editEmployeeDto.setLastName(employee.getLastName());
        editEmployeeDto.setPassword(employee.getPassword());
        editEmployeeDto.setPhoneNumber(employee.getPhoneNumber());
        editEmployeeDto.setIsActive(employee.getActive());
        editEmployeeDto.setPosition(employee.getPosition());
        editEmployeeDto.setPermissions(employee.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toList()));

        return editEmployeeDto;
    }

    public CreateEmployeeDto employeeToCreateEmployeeDto(Employee employee) {
        CreateEmployeeDto createEmployeeDto = new CreateEmployeeDto();
        createEmployeeDto.setFirstName(employee.getFirstName());
        createEmployeeDto.setLastName(employee.getLastName());
        createEmployeeDto.setEmail(employee.getEmail());
        createEmployeeDto.setPhoneNumber(employee.getPhoneNumber());
        createEmployeeDto.setActive(employee.getActive());
        createEmployeeDto.setPosition(employee.getPosition());
        createEmployeeDto.setJmbg(employee.getJmbg());

        return createEmployeeDto;
    }
}
