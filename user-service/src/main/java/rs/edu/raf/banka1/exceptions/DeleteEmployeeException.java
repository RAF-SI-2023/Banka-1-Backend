package rs.edu.raf.banka1.exceptions;

public class DeleteEmployeeException extends BadRequestException {

    public DeleteEmployeeException(Long id) {
        super("Employee with id:" + id + " has already been deactivated");
    }
}
