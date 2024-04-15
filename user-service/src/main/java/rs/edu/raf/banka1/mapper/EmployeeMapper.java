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
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
        if(employee == null) return null;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setUserId(employee.getUserId());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setJmbg(employee.getJmbg());
        employeeDto.setPhoneNumber(employee.getPhoneNumber());
        employeeDto.setPosition(employee.getPosition());
        employeeDto.setOrderlimit(employee.getOrderlimit());
        employeeDto.setLimitNow(employee.getLimitNow());
        employeeDto.setRequireApproval(employee.getRequireApproval());
        employeeDto.setActive(employee.getActive());

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
        employee.setRequireApproval(createEmployeeDto.getRequireApproval());
        employee.setLimitNow(0.0);
        employee.setOrderlimit(createEmployeeDto.getOrderlimit());
        employee.setPosition(createEmployeeDto.getPosition().toLowerCase(Locale.ROOT));

        Set<Permission> permissionSet = new HashSet<>();

        for(String permission : Constants.userPermissions.get(employee.getPosition())){
            Permission perm = permissionRepository.findByName(permission).orElse(null);

            if(perm == null){
                continue;
            }

            permissionSet.add(perm);
        }

        employee.setPermissions(permissionSet);

        return employee;
    }

    public Employee editEmployeeDtoToEmployee(Employee employee, EditEmployeeDto employeeDto) {
        if (employeeDto.getEmail() != null) {
            employee.setEmail(employeeDto.getEmail());
        }

        if (employeeDto.getPassword() != null) {
            employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        }

        if (employeeDto.getFirstName() != null) {
            employee.setFirstName(employeeDto.getFirstName());
        }

        if (employeeDto.getLastName() != null) {
            employee.setLastName(employeeDto.getLastName());
        }

        if (employeeDto.getPosition() != null) {
            employee.setPosition(employeeDto.getPosition());
        }

        if (employeeDto.getPhoneNumber() != null) {
            employee.setPhoneNumber(employeeDto.getPhoneNumber());
        }

        if (employeeDto.getIsActive() != null) {
            employee.setActive(employeeDto.getIsActive());
        }

        if (employeeDto.getLimitNow() != null) {
            employee.setLimitNow(employeeDto.getLimitNow());
        }

        if (employeeDto.getRequireApproval() != null) {
            employee.setRequireApproval(employeeDto.getRequireApproval());
        }

        if (employeeDto.getOrderlimit() != null){
            employee.setOrderlimit(employeeDto.getOrderlimit());
        }

        if (employeeDto.getPermissions() != null) {
            employee.setPermissions(employeeDto.getPermissions()
                    .stream()
                    .map(permission -> permissionRepository.findByName(permission).orElseThrow())
                    .collect(Collectors.toSet()));
        }

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
        editEmployeeDto.setOrderlimit(employee.getOrderlimit());
        editEmployeeDto.setLimitNow(employee.getLimitNow());
        editEmployeeDto.setRequireApproval(employee.getRequireApproval());
        editEmployeeDto.setPermissions(employee.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toList()));

        return editEmployeeDto;
    }
}
