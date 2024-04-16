package rs.edu.raf.banka1.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import rs.edu.raf.banka1.dtos.LimitDto;
import rs.edu.raf.banka1.dtos.NewLimitDto;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.dtos.employee.CreateEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EditEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.requests.ModifyPermissionsRequest;
import rs.edu.raf.banka1.responses.ActivateAccountResponse;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.responses.NewPasswordResponse;

import java.util.List;

public interface EmployeeService extends UserDetailsService {
    EmployeeDto findByEmail(String email);
    List<EmployeeDto> findAll();
    EmployeeDto findById(Long id);
    EmployeeDto findByJwt();
    List<EmployeeDto> search(String email, String firstName, String lastName, String position);
    CreateUserResponse createEmployee(CreateEmployeeDto createEmployeeDto);
    ActivateAccountResponse activateAccount(String token, String password);
    boolean editEmployee(EditEmployeeDto editEmployeeDto);
    Boolean deleteEmployee(Long id);

    List<PermissionDto> findPermissions(Long userId);

    List<PermissionDto> findPermissions(String email);

    Boolean modifyEmployeePermissions(ModifyPermissionsRequest request, Long userId);

    Boolean sendResetPasswordEmail(String email);
    NewPasswordResponse setNewPassword(String token, String password);
    void resetLimitForEmployee(Long employeeId);
    void resetEmployeeLimits();
    LimitDto setOrderLimitForEmployee(NewLimitDto newLimitDto);
    List<LimitDto> getAllLimits();
    Employee getEmployeeEntityByEmail(String email);
}
