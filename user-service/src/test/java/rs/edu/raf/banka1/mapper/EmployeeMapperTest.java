package rs.edu.raf.banka1.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmployeeMapperTest {

    private EmployeeMapper sut;

    @Mock
    private PermissionRepository permissionRepository;

    @BeforeEach
    void setUp() {
        List<Permission> permissions = new ArrayList<>();
        permissionRepository = mock(PermissionRepository.class);
        for(var x: Constants.allPermissions){
            Permission permission = new Permission();
            permission.setName(x);
            when(permissionRepository.findByName(x)).thenReturn(Optional.of(permission));
        }

        sut = new EmployeeMapper(new PermissionMapper(), null, permissionRepository);
    }
    @Test
    void employeeToEmployeeDto() {
        Employee employee = new Employee();
        employee.setUserId(1L);
        employee.setEmail("email");
        employee.setFirstName("firstName");
        employee.setLastName("lastName");
        employee.setJmbg("jmbg");
        employee.setPhoneNumber("phoneNumber");
        employee.setPosition("position");
        employee.setOrderlimit(1.0);
        employee.setLimitNow(1.0);
        employee.setRequireApproval(true);

        EmployeeDto employeeDto = sut.employeeToEmployeeDto(employee);

        assertEquals(employee.getUserId(), employeeDto.getUserId());
        assertEquals(employee.getEmail(), employeeDto.getEmail());
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
        assertEquals(employee.getLastName(), employeeDto.getLastName());
        assertEquals(employee.getJmbg(), employeeDto.getJmbg());
        assertEquals(employee.getPhoneNumber(), employeeDto.getPhoneNumber());
        assertEquals(employee.getPosition(), employeeDto.getPosition());
        assertEquals(employee.getOrderlimit(), employeeDto.getOrderlimit());
        assertEquals(employee.getLimitNow(), employeeDto.getLimitNow());
        assertEquals(employee.getRequireApproval(), employeeDto.getRequireApproval());
    }


}