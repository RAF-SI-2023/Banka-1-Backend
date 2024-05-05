package rs.edu.raf.banka1.exceptions;

public class EmployeeNotFoundException extends NotFoundException{
    public EmployeeNotFoundException(Long employeeId) {
        super("Employee with id: " + employeeId + " not found");
    }
}
