package rs.edu.raf.banka1.exceptions;

public class EmployeeNotFoundException extends RuntimeException{
    public EmployeeNotFoundException(Long employeeId) {
        super("Employee with id: " + employeeId + " not found");
    }
}
