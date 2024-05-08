package rs.edu.raf.banka1.exceptions;

public class ModifyRecipientException extends BadRequestException {

    public ModifyRecipientException(Long id) {
        super("Recipient with " + id + " not found");
    }
}
