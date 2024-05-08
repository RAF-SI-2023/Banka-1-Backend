package rs.edu.raf.banka1.exceptions;

public class RecipientNotFoundException extends NotFoundException{


    public RecipientNotFoundException(Long id) {
        super("Recipient with id: " + id + " not found");
    }

}
