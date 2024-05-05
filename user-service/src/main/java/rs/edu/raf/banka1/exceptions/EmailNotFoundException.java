package rs.edu.raf.banka1.exceptions;

public class EmailNotFoundException extends NotFoundException{
    public EmailNotFoundException(String email) {
        super("User with email: " + email + " not found");
    }
}
